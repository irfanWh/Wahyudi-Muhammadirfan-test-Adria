# Réponses aux questions d'analyse

## 1. Pourquoi l'accuracy seule est-elle insuffisante ?

Les fraudes représentent seulement 5.0% du jeu de données. Un modèle qui prédit toujours « normale » aurait donc une accuracy élevée sans détecter aucune fraude. Il faut examiner en priorité la précision, le rappel, le F1-score et l'average precision de la classe fraude.

## 2. Quel compromis existe entre faux positifs et fraudes non détectées ?

Augmenter le rappel permet de détecter davantage de fraudes, mais produit généralement plus de faux positifs et donc plus de contrôles manuels ou de transactions bloquées à tort. Augmenter la précision réduit ces alertes inutiles, mais peut laisser passer davantage de fraudes. Le choix dépend du coût métier de chaque erreur.

## 3. Quel est l'effet possible d'une diminution du seuil de décision ?

Une diminution du seuil classe davantage d'opérations comme frauduleuses. Le rappel augmente généralement, tandis que la précision peut diminuer à cause d'un plus grand nombre de faux positifs. Le seuil de ce test reste fixé à 0,5 et n'est pas sélectionné à partir de l'ensemble de test.

## 4. Pourquoi privilégier une validation temporelle sur des données bancaires réelles ?

Les comportements des clients et les méthodes de fraude évoluent dans le temps. Une séparation aléatoire peut mélanger passé et futur et produire une estimation trop optimiste. Une validation temporelle entraîne le modèle sur les périodes antérieures et l'évalue sur une période ultérieure, ce qui représente mieux son utilisation réelle et limite les fuites d'information.

## Résultats obtenus

- Précision fraude : 0.600
- Rappel fraude : 0.900
- F1-score fraude : 0.720
- Average precision : 0.936

Ces résultats proviennent d'un jeu synthétique et servent uniquement à valider le pipeline technique.
