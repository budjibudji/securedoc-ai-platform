# SecureDoc AI Platform

**SecureDoc AI** est une plateforme cloud-native basée sur une architecture microservices, conçue pour détecter et anonymiser automatiquement les informations sensibles (PII) dans les documents PDF. Elle utilise le Deep Learning (modèles BERT) et une infrastructure distribuée pour garantir la scalabilité, la sécurité et l'observabilité.

---

## Vue d'ensemble de l'Architecture

Le système repose sur une architecture distribuée orchestrée par **Kubernetes**. Il découple la gestion des fichiers, le traitement IA et l'audit via un système de messagerie asynchrone.

### Flux de Fonctionnement
1.  **Upload :** L'utilisateur s'authentifie via **Keycloak** et téléverse un PDF via le Frontend React.
2.  **Stockage :** Le service **Doc-Manager** stocke les métadonnées dans **PostgreSQL** et transfère le fichier physique vers **MinIO** (stockage objet S3).
3.  **Événement :** Un message est publié dans **RabbitMQ** (via un *Fanout Exchange*).
4.  **Traitement Parallèle :**
    * **AI-Redactor :** Consomme le message, télécharge le fichier, exécute la reconnaissance d'entités nommées (NER) via **DJL & BERT**, redessine le PDF pour masquer les données, et renvoie la version sécurisée.
    * **Audit-Service :** Consomme le même message pour enregistrer l'activité dans **MongoDB** à des fins de conformité.
5.  **Notification :** Le frontend interroge le statut pour permettre à l'utilisateur de télécharger le document traité.

---

## Stack Technique

### Backend & Intelligence Artificielle
* **Java 17 & Spring Boot 3 :** Framework principal des microservices.
* **Deep Java Library (DJL) :** Moteur d'exécution pour les modèles de Deep Learning en Java.
* **HuggingFace BERT :** Modèle pré-entraîné (`dslim/bert-base-NER`) pour la reconnaissance d'entités.
* **Apache PDFBox :** Manipulation des flux de contenu PDF et extraction de texte.

### Données & Messagerie
* **PostgreSQL :** Base relationnelle pour les métadonnées documentaires.
* **MongoDB :** Base NoSQL pour les logs d'audit (Traçabilité).
* **MinIO :** Stockage objet compatible S3 auto-hébergé.
* **RabbitMQ :** Broker de messages pour la communication asynchrone (Pattern Fanout).

### Infrastructure & DevOps
* **Docker :** Images optimisées via *Multi-stage builds* (Base Eclipse Temurin).
* **Kubernetes (K8s) :** Orchestration (Deployments, Services, ConfigMaps, PVCs).
* **AWS (EC2) :** Infrastructure d'hébergement.
* **GitLab CI/CD :** Pipelines automatisés pour le Build & Deploy sur AWS.
* **Nginx & Certbot :** Reverse Proxy et gestion SSL (Let's Encrypt).

### Observability & Sécurité
* **Keycloak :** Gestion des identités (IAM) via OIDC/OAuth2.
* **Spring Security :** Resource Server pour la validation des tokens JWT (Architecture Zero Trust).
* **Prometheus :** Collecte de métriques (Scraping).
* **Grafana :** Tableaux de bord de visualisation (JVM, CPU, RAM, Débit requêtes).

---

## Détail des Microservices

| Service | Port Interne | Description |
| :--- | :--- | :--- |
| **Doc-Manager** | `8081` | Point d'entrée. Gère les uploads, le client MinIO et déclenche les événements RabbitMQ. |
| **AI-Redactor** | `8082` | Le moteur IA. Exécute le modèle BERT pour identifier noms/emails et appliquer la redaction. |
| **Audit-Service** | `8083` | Le service de traçabilité. Logue chaque événement système dans MongoDB. |
| **Frontend** | `3000` | Application SPA React.js sécurisée avec `keycloak-js`. |

---

## Cloud & Déploiement (AWS)

Le projet est déployé dans un environnement de production sur **AWS EC2** :

* **DNS & Routage :** Géré via **AWS Route 53** avec des sous-domaines dédiés (`api.`, `front.`, `grafana.`).
* **Pipeline CI/CD :** Un **GitLab Runner** mutualisé installé sur l'instance EC2 écoute les commits, construit les images Docker et met à jour le cluster Kubernetes automatiquement.
* **Sécurité Réseau :** Le trafic est sécurisé via **Nginx** (Reverse Proxy) et des certificats **SSL/TLS** générés automatiquement par Certbot.

---

## Observabilité (Monitoring)

Une stack complète a été mise en place pour surveiller la santé du système en temps réel :
1.  **Instrumentation :** Utilisation de Spring Boot Actuator et Micrometer dans chaque service Java.
2.  **Collecte :** Prometheus découvre dynamiquement les pods dans K8s et récupère les métriques.
3.  **Visualisation :** Dashboards Grafana personnalisés corrélant la santé des services (UP/DOWN) avec la consommation des ressources.

---

## Installation Locale (Dev)

### Prérequis
* Docker Desktop (avec Kubernetes activé)
* Java 17
* Maven

### 1. Clonage & Build
```bash
git clone [https://github.com/votre-username/securedoc-ai.git](https://github.com/votre-username/securedoc-ai.git)
cd securedoc-ai
# Compilation de tous les services
mvn clean package -DskipTests
