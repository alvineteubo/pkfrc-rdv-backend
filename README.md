# 🗓️ RDV Backend API

API REST de gestion des rendez-vous développée avec Java 21 et Spring Boot 3.x.

## 📋 Table des matières

- [Technologies utilisées](#technologies-utilisées)
- [Prérequis](#prérequis)
- [Installation et lancement](#installation-et-lancement)
- [Structure du projet](#structure-du-projet)
- [Choix de conception](#choix-de-conception)
- [Documentation API](#documentation-api)
- [Exécution des tests](#exécution-des-tests)

---

## 🛠️ Technologies utilisées

| Technologie | Version |
|---|---|
| Java | 21 |
| Spring Boot | 3.5.14 |
| PostgreSQL | 18.4 |
| Flyway | 11.x |

---

## ✅ Prérequis

- Java 21
- Maven 3.8+
- PostgreSQL 18.4

---

## 🚀 Installation et lancement

### 1. Cloner le projet

```bash
git clone https://github.com/votre-repo/pkfrc-rdv-backend.git
cd pkfrc-rdv-backend
```

### 2. Créer les bases de données

Dans pgAdmin ou psql :

```sql
-- Base principale
CREATE DATABASE pkfrc_rdv;

-- Base de test
CREATE DATABASE pkfrc_rdv_test;
```

### 3. Configurer les variables de connexion

Dans `src/main/resources/application.yaml` :

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/pkfrc_rdv
    username: postgres
    password: votre_mot_de_passe
```

Dans `src/test/resources/application-test.yaml` :

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/pkfrc_rdv_test
    username: postgres
    password: votre_mot_de_passe
```

### 4. Lancer l'application

```bash
mvn spring-boot:run
```

Flyway exécute automatiquement les migrations :
- `V1__create_tables.sql` — création des tables
- `V2__insert_data.sql` — données de référence (services, plages horaires)
- `V3__fix_version_default.sql` — correction de la valeur par défaut du champ `version`

### 5. Accéder à l'API

Swagger UI  → http://localhost:9095/swagger-ui.html  
API Docs    → http://localhost:9095/v3/api-docs

---

## 📁 Structure du projet

```
src/
├── main/
│   ├── java/com/pkrfc/rdv_backend/
│   │   ├── config/          # Configuration (WebConfig, SwaggerConfig)
│   │   ├── controllers/     # Controllers REST
│   │   ├── exceptions/      # Gestion des erreurs (GlobalExceptionHandler)
│   │   ├── models/
│   │   │   ├── dtos/        # Records Java 21 (requests/responses)
│   │   │   ├── entities/    # Entités JPA
│   │   │   ├── mappers/     # Mappers statiques
│   │   │   └── repositories/# Repositories Spring Data JPA
│   │   ├── services/
│   │   │   ├── inter/       # Interfaces des services
│   │   │   └── impl/        # Implémentations + ServiceHelper
│   │   └── utils/           # Utilitaires (I18nUtils)
│   └── resources/
│       ├── db/migration/    # Scripts Flyway
│       ├── i18n/            # Messages internationalisés (fr/en)
│       └── application.yaml
└── test/
    ├── java/com/pkrfc/rdv_backend/
    │   ├── services/        # Tests unitaires
    │   └── integrationTest/ # Tests d'intégration
    └── resources/
        └── application-test.yaml
```

---

## 🏗️ Choix de conception

### Architecture
- **Pattern Interface + Implémentation** pour tous les services (principe D de SOLID)
- **Records Java 21** pour tous les DTOs — immuables et concis
- **Mappers statiques** — séparation claire entre entités et DTOs
- **`ServiceHelper`** — composant partagé qui centralise la création et la mise à jour de `Utilisateur`, évitant la duplication entre `GestionClientServiceImpl` et `GestionResponsableServiceImpl`

### Gestion de la concurrence
Protection contre les prises de RDV simultanées sur le même créneau :

| Niveau | Mécanisme |
|---|---|
| Applicatif | `@Lock(PESSIMISTIC_WRITE)` sur le chargement du `Responsable` — sérialise les transactions concurrentes par responsable |
| Applicatif | Vérification de disponibilité après acquisition du verrou |

### Règles métier
- Date RDV minimum **J+2**
- Plage horaire **déduite automatiquement** depuis l'heure (08h-16h, créneaux d'1h)
- Maximum **2 participants** par RDV
- Un client ne peut pas être inscrit **deux fois** au même RDV

### Internationalisation
Messages disponibles en **français** (défaut) et **anglais** via l'en-tête HTTP `codeisolang`.

---

## 📖 Documentation API

### Endpoints principaux

| Méthode | URL | Description |
|---|---|---|
| `POST` | `/api/utilisateur/create` | Créer ou modifier un utilisateur |
| `GET` | `/api/utilisateur/{ref}` | Récupérer un utilisateur par ref |
| `DELETE` | `/api/utilisateur/{ref}` | Supprimer un utilisateur |
| `POST` | `/api/client/create` | Créer ou modifier un client |
| `GET` | `/api/client/{ref}` | Récupérer un client par ref |
| `DELETE` | `/api/client/{ref}` | Supprimer un client |
| `POST` | `/api/responsable/create` | Créer ou modifier un responsable |
| `GET` | `/api/responsable/{ref}` | Récupérer un responsable par ref |
| `DELETE` | `/api/responsable/{ref}` | Supprimer un responsable |
| `GET` | `/api/plages-horaire/plage_horaires` | Lister les plages horaires |
| `POST` | `/api/rendez-vous/create` | Prendre un rendez-vous |
| `PATCH` | `/api/rendez-vous/{ref}/statut` | Changer le statut |
| `POST` | `/api/rendez-vous/{refRdv}/participants/{refClient}` | Ajouter un participant |
| `DELETE` | `/api/rendez-vous/{refRdv}/participants/{refClient}` | Retirer un participant |

### Exemple de prise de RDV

```json
POST /api/rendez-vous/create
{
  "refClient": "uuid-client",
  "refService": "uuid-service",
  "refResponsable": "uuid-responsable",
  "dateRdv": "2026-05-28T09:00:00",
  "motif": "Demande de document administratif"
}
```

---

## 🧪 Exécution des tests

### Tous les tests

```bash
mvn test
```

### Tests unitaires uniquement

```bash
mvn test -Dtest="com.pkrfc.rdv_backend.services.*"
```

### Tests d'intégration uniquement

```bash
mvn test -Dtest=RendezVousIntegrationTest
```

### Tests par classe

```bash
mvn test -Dtest=GestionUtilisateurServiceTest
mvn test -Dtest=GestionClientServiceTest
mvn test -Dtest=GestionResponsableServiceTest
mvn test -Dtest=GestionRendezVousServiceTest
```

### Couverture des tests

| Classe | Tests unitaires | Tests d'intégration |
|---|---|---|
| `GestionUtilisateurService` | ✅ 11 tests | — |
| `GestionClientService` | ✅ 9 tests | — |
| `GestionResponsableService` | ✅ 10 tests | — |
| `GestionRendezVousService` | ✅ 13 tests | — |
| Flux RDV complet | — | ✅ 4 tests |

---

## 👤 Auteur

**PKRFC** — Projet d'examen Spring Boot 3.x / Java 21