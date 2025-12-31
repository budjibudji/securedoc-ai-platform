# 🛡️ SecureDoc AI Platform

![Java](https://img.shields.io/badge/Java-17-orange) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.0-green) ![Kubernetes](https://img.shields.io/badge/Kubernetes-Orchestration-blue) ![AWS](https://img.shields.io/badge/AWS-Production-232F3E) ![AI](https://img.shields.io/badge/AI-BERT_Model-red)

**SecureDoc AI** est une plateforme cloud-native basée sur une architecture microservices, conçue pour détecter et anonymiser automatiquement les informations sensibles (PII) dans les documents PDF. Elle adopte une approche *"Privacy by Design"* en combinant le Deep Learning et une infrastructure distribuée.

---

## 🏗️ Architecture & Flux

Le système repose sur une architecture distribuée orchestrée par **Kubernetes**, découplant la gestion des fichiers, l'IA et l'audit.

### 🔄 Workflow de traitement
1.  📤 **Upload :** L'utilisateur s'authentifie via **Keycloak** et envoie un PDF via le Frontend React.
2.  💾 **Stockage :** Le service **Doc-Manager** sécurise les métadonnées dans **PostgreSQL** et le fichier dans **MinIO** (S3).
3.  ⚡ **Événement :** Un message est publié instantanément dans **RabbitMQ** (Fanout).
4.  🧠 **Traitement Intelligent :**
    * **AI-Redactor** analyse le document avec un modèle **BERT**, détecte les entités (Noms, Emails) et redessine le PDF pour les masquer.
    * **Audit-Service** intercepte le même message pour garantir la traçabilité dans **MongoDB**.
5.  🔔 **Notification :** Le frontend notifie l'utilisateur que le document sécurisé est prêt.

---

## 🛠️ Stack Technique

### 🧠 Backend & Intelligence Artificielle
* **Java 17 & Spring Boot 3 :** Cœur des microservices.
* **Deep Java Library (DJL) :** Moteur d'IA pour Java.
* **HuggingFace BERT :** Modèle NER (`dslim/bert-base-NER`) pour la détection précise.
* **Apache PDFBox :** Moteur de manipulation et de rédaction des PDF.

### 🗄️ Données & Messagerie
* **PostgreSQL :** Données relationnelles (Métadonnées).
* **MongoDB :** Logs d'audit flexibles (NoSQL).
* **MinIO :** Stockage objet S3 auto-hébergé.
* **RabbitMQ :** Orchestration asynchrone des services.

### 🚀 DevOps & Cloud
* **Docker :** Images légères (Multi-stage builds).
* **Kubernetes (K8s) :** Orchestration (Pods, Services, PVC).
* **AWS (EC2 & Route53) :** Infrastructure de production.
* **GitLab CI/CD :** Pipeline automatisé (Build -> Deploy).
* **Nginx :** Reverse Proxy & SSL (Let's Encrypt).

### 👁️ Observabilité & Sécurité
* **Keycloak :** Gestion des identités (OIDC/OAuth2).
* **Spring Security :** Validation "Zero Trust" des tokens JWT.
* **Prometheus & Grafana :** Monitoring temps réel (CPU, JVM, Requêtes).

---

## 📦 Détail des Microservices

| Service | Port | Rôle Principal |
| :--- | :--- | :--- |
| **Doc-Manager** | `8081` | 👮 Chef d'orchestre : Uploads & Dispatching. |
| **AI-Redactor** | `8082` | 🧠 Cerveau : Inférence BERT & Rédaction PDF. |
| **Audit-Service** | `8083` | 📝 Mémoire : Archivage des logs de conformité. |
| **Frontend** | `3000` | 💻 Interface : UX React.js sécurisée. |

---

## ☁️ Déploiement Cloud (AWS)

Le projet est en production sur **AWS EC2** :

* 🌐 **DNS :** Gestion via **Route 53** (`api.mahfoud.click`, `front...`).
* 🤖 **CI/CD :** Runner GitLab mutualisé sur l'instance pour le déploiement continu.
* 🔒 **HTTPS :** Certificats SSL automatiques via Certbot & Nginx.

---

## 📈 Monitoring

Une stack complète pour ne jamais piloter à l'aveugle :
* 🌡️ **Sondes :** Actuator & Micrometer.
* 🔍 **Scraping :** Prometheus découvre les pods dynamiquement.
* 📊 **Dashboards :** Grafana visualise la santé (UP/DOWN) et la charge.

---

## 💻 Installation Locale (Dev)

### 📋 Prérequis
* Docker Desktop (Kubernetes activé)
* Java 17 & Maven

### 1️⃣ Clonage & Build
```bash
git clone [https://github.com/votre-repo/securedoc-ai.git](https://github.com/votre-repo/securedoc-ai.git)
cd securedoc-ai
mvn clean package -DskipTests
