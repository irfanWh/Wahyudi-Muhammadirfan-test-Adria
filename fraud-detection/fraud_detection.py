from __future__ import annotations

import argparse
import json
from pathlib import Path

import matplotlib

matplotlib.use("Agg")

import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns
from sklearn.compose import ColumnTransformer
from sklearn.impute import SimpleImputer
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import (
    average_precision_score,
    classification_report,
    confusion_matrix,
    f1_score,
    precision_recall_curve,
    precision_score,
    recall_score,
)
from sklearn.model_selection import train_test_split
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import OneHotEncoder, StandardScaler


RANDOM_STATE = 42
TARGET = "is_fraud"
IDENTIFIERS = ["transaction_id", "account_id"]
REQUIRED_COLUMNS = [
    "transaction_id",
    "account_id",
    "amount",
    "hour",
    "country",
    "merchant_category",
    "transactions_last_24h",
    "average_amount_last_7d",
    TARGET,
]


def parse_args() -> argparse.Namespace:
    project_root = Path(__file__).resolve().parents[1]
    parser = argparse.ArgumentParser(
        description="Entraine et evalue un modele simple de detection de fraude bancaire."
    )
    parser.add_argument(
        "--data",
        type=Path,
        default=project_root / "bank_transactions.csv",
        help="Chemin du fichier CSV.",
    )
    parser.add_argument(
        "--output-dir",
        type=Path,
        default=Path(__file__).resolve().parent / "results",
        help="Dossier des resultats.",
    )
    return parser.parse_args()


def load_data(path: Path) -> pd.DataFrame:
    if not path.exists():
        raise FileNotFoundError(f"Fichier introuvable : {path}")

    data = pd.read_csv(path)
    missing_columns = sorted(set(REQUIRED_COLUMNS) - set(data.columns))
    if missing_columns:
        raise ValueError(f"Colonnes manquantes : {', '.join(missing_columns)}")

    if not set(data[TARGET].dropna().unique()).issubset({0, 1}):
        raise ValueError("La cible is_fraud doit contenir uniquement 0 et 1.")

    return data


def build_pipeline(numeric_features: list[str], categorical_features: list[str]) -> Pipeline:
    numeric_pipeline = Pipeline(
        steps=[
            ("imputer", SimpleImputer(strategy="median")),
            ("scaler", StandardScaler()),
        ]
    )
    categorical_pipeline = Pipeline(
        steps=[
            ("imputer", SimpleImputer(strategy="most_frequent")),
            ("encoder", OneHotEncoder(handle_unknown="ignore")),
        ]
    )

    preprocessor = ColumnTransformer(
        transformers=[
            ("numeric", numeric_pipeline, numeric_features),
            ("categorical", categorical_pipeline, categorical_features),
        ]
    )

    return Pipeline(
        steps=[
            ("preprocessor", preprocessor),
            (
                "classifier",
                LogisticRegression(
                    class_weight="balanced",
                    max_iter=1000,
                    random_state=RANDOM_STATE,
                ),
            ),
        ]
    )


def save_confusion_matrix(y_test: pd.Series, y_pred: list[int], path: Path) -> None:
    matrix = confusion_matrix(y_test, y_pred, labels=[0, 1])
    plt.figure(figsize=(6, 5))
    sns.heatmap(
        matrix,
        annot=True,
        fmt="d",
        cmap="Blues",
        cbar=False,
        xticklabels=["Normale", "Fraude"],
        yticklabels=["Normale", "Fraude"],
    )
    plt.title("Matrice de confusion")
    plt.xlabel("Prediction")
    plt.ylabel("Valeur reelle")
    plt.tight_layout()
    plt.savefig(path, dpi=160)
    plt.close()


def save_precision_recall_curve(
    y_test: pd.Series,
    probabilities: list[float],
    average_precision: float,
    path: Path,
) -> None:
    precision, recall, _ = precision_recall_curve(y_test, probabilities)
    baseline = float(y_test.mean())

    plt.figure(figsize=(6, 5))
    plt.plot(recall, precision, color="#1658B8", label=f"Modele (AP={average_precision:.3f})")
    plt.axhline(
        baseline,
        color="#DC2626",
        linestyle="--",
        label=f"Reference ({baseline:.3f})",
    )
    plt.title("Courbe precision-rappel")
    plt.xlabel("Rappel")
    plt.ylabel("Precision")
    plt.legend()
    plt.tight_layout()
    plt.savefig(path, dpi=160)
    plt.close()


def save_analysis_answers(path: Path, metrics: dict) -> None:
    content = f"""# Réponses aux questions d'analyse

## 1. Pourquoi l'accuracy seule est-elle insuffisante ?

Les fraudes représentent seulement {metrics['dataset']['fraud_rate']:.1%} du jeu de données. Un modèle qui prédit toujours « normale » aurait donc une accuracy élevée sans détecter aucune fraude. Il faut examiner en priorité la précision, le rappel, le F1-score et l'average precision de la classe fraude.

## 2. Quel compromis existe entre faux positifs et fraudes non détectées ?

Augmenter le rappel permet de détecter davantage de fraudes, mais produit généralement plus de faux positifs et donc plus de contrôles manuels ou de transactions bloquées à tort. Augmenter la précision réduit ces alertes inutiles, mais peut laisser passer davantage de fraudes. Le choix dépend du coût métier de chaque erreur.

## 3. Quel est l'effet possible d'une diminution du seuil de décision ?

Une diminution du seuil classe davantage d'opérations comme frauduleuses. Le rappel augmente généralement, tandis que la précision peut diminuer à cause d'un plus grand nombre de faux positifs. Le seuil de ce test reste fixé à 0,5 et n'est pas sélectionné à partir de l'ensemble de test.

## 4. Pourquoi privilégier une validation temporelle sur des données bancaires réelles ?

Les comportements des clients et les méthodes de fraude évoluent dans le temps. Une séparation aléatoire peut mélanger passé et futur et produire une estimation trop optimiste. Une validation temporelle entraîne le modèle sur les périodes antérieures et l'évalue sur une période ultérieure, ce qui représente mieux son utilisation réelle et limite les fuites d'information.

## Résultats obtenus

- Précision fraude : {metrics['test_metrics']['fraud_precision']:.3f}
- Rappel fraude : {metrics['test_metrics']['fraud_recall']:.3f}
- F1-score fraude : {metrics['test_metrics']['fraud_f1']:.3f}
- Average precision : {metrics['test_metrics']['average_precision']:.3f}

Ces résultats proviennent d'un jeu synthétique et servent uniquement à valider le pipeline technique.
"""
    path.write_text(content, encoding="utf-8")


def main() -> None:
    args = parse_args()
    args.output_dir.mkdir(parents=True, exist_ok=True)

    data = load_data(args.data)
    missing_values = data.isna().sum().astype(int).to_dict()
    class_counts = data[TARGET].value_counts().sort_index().astype(int).to_dict()
    fraud_rate = float(data[TARGET].mean())

    print(f"Nombre de lignes : {len(data)}")
    print(f"Proportion de fraudes : {fraud_rate:.2%}")
    print("Valeurs manquantes :")
    print(pd.Series(missing_values).to_string())

    features = data.drop(columns=IDENTIFIERS + [TARGET])
    target = data[TARGET]

    x_train, x_test, y_train, y_test = train_test_split(
        features,
        target,
        test_size=0.20,
        random_state=RANDOM_STATE,
        stratify=target,
    )

    categorical_features = features.select_dtypes(include=["object", "category"]).columns.tolist()
    numeric_features = features.select_dtypes(exclude=["object", "category"]).columns.tolist()

    model = build_pipeline(numeric_features, categorical_features)
    model.fit(x_train, y_train)

    predictions = model.predict(x_test)
    probabilities = model.predict_proba(x_test)[:, 1]
    matrix = confusion_matrix(y_test, predictions, labels=[0, 1])

    metrics = {
        "dataset": {
            "rows": int(len(data)),
            "fraud_count": int(class_counts.get(1, 0)),
            "fraud_rate": fraud_rate,
            "missing_values": missing_values,
        },
        "split": {
            "strategy": "stratified_random_split",
            "random_state": RANDOM_STATE,
            "train_rows": int(len(x_train)),
            "test_rows": int(len(x_test)),
            "test_fraud_rate": float(y_test.mean()),
        },
        "model": {
            "name": "LogisticRegression",
            "class_weight": "balanced",
            "decision_threshold": 0.5,
            "numeric_features": numeric_features,
            "categorical_features": categorical_features,
            "excluded_identifiers": IDENTIFIERS,
        },
        "test_metrics": {
            "fraud_precision": float(precision_score(y_test, predictions, zero_division=0)),
            "fraud_recall": float(recall_score(y_test, predictions, zero_division=0)),
            "fraud_f1": float(f1_score(y_test, predictions, zero_division=0)),
            "average_precision": float(average_precision_score(y_test, probabilities)),
            "confusion_matrix": matrix.tolist(),
        },
    }

    report = classification_report(
        y_test,
        predictions,
        labels=[0, 1],
        target_names=["normale", "fraude"],
        output_dict=True,
        zero_division=0,
    )

    (args.output_dir / "metrics.json").write_text(
        json.dumps(metrics, indent=2, ensure_ascii=False),
        encoding="utf-8",
    )
    pd.DataFrame(report).transpose().to_csv(
        args.output_dir / "classification_report.csv",
        index=True,
    )
    save_confusion_matrix(
        y_test,
        predictions,
        args.output_dir / "confusion_matrix.png",
    )
    save_precision_recall_curve(
        y_test,
        probabilities,
        metrics["test_metrics"]["average_precision"],
        args.output_dir / "precision_recall_curve.png",
    )
    save_analysis_answers(args.output_dir / "analysis_answers.md", metrics)

    print("\nRésultats sur l'ensemble de test :")
    print(f"Précision fraude : {metrics['test_metrics']['fraud_precision']:.3f}")
    print(f"Rappel fraude : {metrics['test_metrics']['fraud_recall']:.3f}")
    print(f"F1-score fraude : {metrics['test_metrics']['fraud_f1']:.3f}")
    print(f"Average precision : {metrics['test_metrics']['average_precision']:.3f}")
    print(f"Matrice de confusion : {matrix.tolist()}")
    print(f"Fichiers générés dans : {args.output_dir}")


if __name__ == "__main__":
    main()
