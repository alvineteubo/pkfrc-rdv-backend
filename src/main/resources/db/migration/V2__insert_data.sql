-- Insertion des 5 services
INSERT INTO service (ref_service, code, nom, description)
VALUES (gen_random_uuid()::VARCHAR, 'ARCH', 'Archives', 'Service de gestion des archives'),
       (gen_random_uuid()::VARCHAR, 'DAF', 'DAF', 'Direction des Affaires Financières'),
       (gen_random_uuid()::VARCHAR, 'RH', 'RH', 'Service des Ressources Humaines'),
       (gen_random_uuid()::VARCHAR, 'COMPTA', 'Comptabilité', 'Service de comptabilité'),
       (gen_random_uuid()::VARCHAR, 'AFF_SOC', 'Affaires sociales', 'Service des affaires sociales');

-- Insertion des plages horaires 08h-16h
INSERT INTO plage_horaire (id_plage_horaire, heure_debut, heure_fin)
VALUES (gen_random_uuid()::VARCHAR, '08:00', '09:00'),
       (gen_random_uuid()::VARCHAR, '09:00', '10:00'),
       (gen_random_uuid()::VARCHAR, '10:00', '11:00'),
       (gen_random_uuid()::VARCHAR, '11:00', '12:00'),
       (gen_random_uuid()::VARCHAR, '12:00', '13:00'),
       (gen_random_uuid()::VARCHAR, '13:00', '14:00'),
       (gen_random_uuid()::VARCHAR, '14:00', '15:00'),
       (gen_random_uuid()::VARCHAR, '15:00', '16:00');