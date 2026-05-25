-- Correction : version = NULL pour les lignes insérées sans valeur de version
UPDATE service        SET version = 0 WHERE version IS NULL;
UPDATE plage_horaire  SET version = 0 WHERE version IS NULL;
UPDATE utilisateur    SET version = 0 WHERE version IS NULL;
UPDATE client         SET version = 0 WHERE version IS NULL;
UPDATE responsable    SET version = 0 WHERE version IS NULL;
UPDATE rendez_vous    SET version = 0 WHERE version IS NULL;
UPDATE rendez_vous_participant SET version = 0 WHERE version IS NULL;

-- Ajout de la contrainte DEFAULT pour éviter le problème sur les futurs inserts directs
ALTER TABLE service               ALTER COLUMN version SET DEFAULT 0;
ALTER TABLE plage_horaire         ALTER COLUMN version SET DEFAULT 0;
ALTER TABLE utilisateur           ALTER COLUMN version SET DEFAULT 0;
ALTER TABLE client                ALTER COLUMN version SET DEFAULT 0;
ALTER TABLE responsable           ALTER COLUMN version SET DEFAULT 0;
ALTER TABLE rendez_vous           ALTER COLUMN version SET DEFAULT 0;
ALTER TABLE rendez_vous_participant ALTER COLUMN version SET DEFAULT 0;