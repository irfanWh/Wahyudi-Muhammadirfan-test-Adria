# Partie I - DevOps

## Secrets et configuration

La configuration non sensible (ports, URLs internes et paramètres métier) peut être versionnée ou fournie par Config Server. Les secrets (clé OpenAI, mots de passe et jetons) ne doivent jamais être commités : ils sont injectés avec un fichier `.env` local ignoré, les secrets GitHub Actions ou un gestionnaire de secrets.

## Contrôles de santé

- **Démarrage** : indique que l'application a terminé son initialisation.
- **Disponibilité** : indique qu'elle peut recevoir du trafic et joindre ses dépendances indispensables.
- **Vitalité** : indique que le processus fonctionne ; en cas d'échec durable, le conteneur peut être redémarré.

Avec Spring Boot Actuator, ces contrôles peuvent être exposés par `/actuator/health`, notamment avec les groupes `startup`, `readiness` et `liveness`.

## Logs, métriques et traces

Les services écrivent leurs logs sur la sortie standard pour permettre leur collecte centralisée. Micrometer et Prometheus peuvent collecter les métriques, puis Grafana les affiche. OpenTelemetry permet de propager un identifiant de trace entre la Gateway, les microservices et Kafka afin de suivre une requête distribuée.

## Stratégie de rollback

Chaque image est publiée avec un tag immuable correspondant au SHA Git. En cas d'échec après déploiement, on redéploie le dernier tag stable et on vérifie les contrôles de santé. Les migrations de données doivent rester rétrocompatibles afin que l'ancienne version puisse redémarrer sans perte de données.

## Exécution locale

Créer `.env` à partir de `.env.example`, renseigner la clé OpenAI, puis exécuter :

```bash
docker compose up --build
```

Arrêt de l'application :

```bash
docker compose down
```
