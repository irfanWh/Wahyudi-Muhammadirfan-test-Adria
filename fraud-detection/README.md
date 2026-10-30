# Détection de fraude bancaire

Cette partie charge `bank_transactions.csv`, affiche la proportion de fraudes et les valeurs manquantes, puis entraîne une régression logistique dans un pipeline `scikit-learn`.

Le pipeline applique l'imputation, l'encodage des catégories et la normalisation. Les identifiants sont exclus. La séparation entraînement/test est stratifiée et le prétraitement est ajusté uniquement sur l'ensemble d'entraînement.

## Exécution

Depuis la racine du projet :

```bash
python3 -m venv fraud-detection/.venv
source fraud-detection/.venv/bin/activate
pip install -r fraud-detection/requirements.txt
python fraud-detection/fraud_detection.py
```

Les résultats sont générés dans `fraud-detection/results/` :

- `metrics.json`
- `classification_report.csv`
- `confusion_matrix.png`
- `precision_recall_curve.png`
- `analysis_answers.md`

Le jeu de données fourni dans ce projet est synthétique. Les résultats servent à démontrer le pipeline et ne doivent pas être interprétés comme les performances d'un système bancaire réel.
