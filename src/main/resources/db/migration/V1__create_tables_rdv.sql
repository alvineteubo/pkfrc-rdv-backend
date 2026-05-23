CREATE TABLE client
(
    ref_client      VARCHAR(255) NOT NULL,
    version         BIGINT,
    created_by      VARCHAR(255),
    created_at      TIMESTAMP WITHOUT TIME ZONE,
    updated_at      TIMESTAMP WITHOUT TIME ZONE,
    updated_by      VARCHAR(255),
    ref_utilisateur VARCHAR(255) NOT NULL,
    CONSTRAINT pk_client PRIMARY KEY (ref_client)
);

CREATE TABLE plage_horaire
(
    id_plage_horaire VARCHAR(255) NOT NULL,
    version          BIGINT,
    created_by       VARCHAR(255),
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    updated_at       TIMESTAMP WITHOUT TIME ZONE,
    updated_by       VARCHAR(255),
    heure_debut      time WITHOUT TIME ZONE NOT NULL,
    heure_fin        time WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_plage_horaire PRIMARY KEY (id_plage_horaire)
);

CREATE TABLE rendez_vous
(
    ref_rendez_vous VARCHAR(255) NOT NULL,
    version         BIGINT,
    created_by      VARCHAR(255),
    created_at      TIMESTAMP WITHOUT TIME ZONE,
    updated_at      TIMESTAMP WITHOUT TIME ZONE,
    updated_by      VARCHAR(255),
    ref_responsable VARCHAR(255) NOT NULL,
    ref_service     VARCHAR(255) NOT NULL,
    id_plage        VARCHAR(255) NOT NULL,
    date_rdv        date         NOT NULL,
    motif           VARCHAR(500) NOT NULL,
    statut          VARCHAR(255) NOT NULL,
    CONSTRAINT pk_rendez_vous PRIMARY KEY (ref_rendez_vous)
);

CREATE TABLE rendez_vous_participant
(
    id_participant  VARCHAR(255) NOT NULL,
    version         BIGINT,
    created_by      VARCHAR(255),
    created_at      TIMESTAMP WITHOUT TIME ZONE,
    updated_at      TIMESTAMP WITHOUT TIME ZONE,
    updated_by      VARCHAR(255),
    ref_rendez_vous VARCHAR(255) NOT NULL,
    ref_client      VARCHAR(255) NOT NULL,
    CONSTRAINT pk_rendez_vous_participant PRIMARY KEY (id_participant)
);

CREATE TABLE responsable
(
    ref_responsable VARCHAR(255) NOT NULL,
    version         BIGINT,
    created_by      VARCHAR(255),
    created_at      TIMESTAMP WITHOUT TIME ZONE,
    updated_at      TIMESTAMP WITHOUT TIME ZONE,
    updated_by      VARCHAR(255),
    ref_utilisateur VARCHAR(255) NOT NULL,
    ref_service     VARCHAR(255) NOT NULL,
    CONSTRAINT pk_responsable PRIMARY KEY (ref_responsable)
);

CREATE TABLE service
(
    ref_service VARCHAR(255) NOT NULL,
    version     BIGINT,
    created_by  VARCHAR(255),
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    updated_by  VARCHAR(255),
    code        VARCHAR(50)  NOT NULL,
    nom         VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    CONSTRAINT pk_service PRIMARY KEY (ref_service)
);

CREATE TABLE utilisateur
(
    ref_utilisateur VARCHAR(255) NOT NULL,
    version         BIGINT,
    created_by      VARCHAR(255),
    created_at      TIMESTAMP WITHOUT TIME ZONE,
    updated_at      TIMESTAMP WITHOUT TIME ZONE,
    updated_by      VARCHAR(255),
    nom             VARCHAR(255) NOT NULL,
    prenom          VARCHAR(255),
    email           VARCHAR(255) NOT NULL,
    telephone       BIGINT,
    CONSTRAINT pk_utilisateur PRIMARY KEY (ref_utilisateur)
);

ALTER TABLE client
    ADD CONSTRAINT uc_client_ref_utilisateur UNIQUE (ref_utilisateur);

ALTER TABLE responsable
    ADD CONSTRAINT uc_responsable_ref_utilisateur UNIQUE (ref_utilisateur);

ALTER TABLE service
    ADD CONSTRAINT uc_service_code UNIQUE (code);

ALTER TABLE utilisateur
    ADD CONSTRAINT uc_utilisateur_email UNIQUE (email);

ALTER TABLE rendez_vous_participant
    ADD CONSTRAINT uq_rdv_client UNIQUE (ref_rendez_vous, ref_client);

ALTER TABLE rendez_vous
    ADD CONSTRAINT uq_responsable_plage_date UNIQUE (ref_responsable, id_plage, date_rdv);

ALTER TABLE client
    ADD CONSTRAINT FK_CLIENT_ON_REF_UTILISATEUR FOREIGN KEY (ref_utilisateur) REFERENCES utilisateur (ref_utilisateur);

ALTER TABLE rendez_vous
    ADD CONSTRAINT FK_RENDEZ_VOUS_ON_ID_PLAGE FOREIGN KEY (id_plage) REFERENCES plage_horaire (id_plage_horaire);

ALTER TABLE rendez_vous
    ADD CONSTRAINT FK_RENDEZ_VOUS_ON_REF_RESPONSABLE FOREIGN KEY (ref_responsable) REFERENCES responsable (ref_responsable);

ALTER TABLE rendez_vous
    ADD CONSTRAINT FK_RENDEZ_VOUS_ON_REF_SERVICE FOREIGN KEY (ref_service) REFERENCES service (ref_service);

ALTER TABLE rendez_vous_participant
    ADD CONSTRAINT FK_RENDEZ_VOUS_PARTICIPANT_ON_REF_CLIENT FOREIGN KEY (ref_client) REFERENCES client (ref_client);

ALTER TABLE rendez_vous_participant
    ADD CONSTRAINT FK_RENDEZ_VOUS_PARTICIPANT_ON_REF_RENDEZ_VOUS FOREIGN KEY (ref_rendez_vous) REFERENCES rendez_vous (ref_rendez_vous);

ALTER TABLE responsable
    ADD CONSTRAINT FK_RESPONSABLE_ON_REF_SERVICE FOREIGN KEY (ref_service) REFERENCES service (ref_service);

ALTER TABLE responsable
    ADD CONSTRAINT FK_RESPONSABLE_ON_REF_UTILISATEUR FOREIGN KEY (ref_utilisateur) REFERENCES utilisateur (ref_utilisateur);