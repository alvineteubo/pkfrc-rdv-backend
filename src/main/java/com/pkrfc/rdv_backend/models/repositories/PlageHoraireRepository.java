package com.pkrfc.rdv_backend.models.repositories;

import com.pkrfc.rdv_backend.models.entities.PlageHoraire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.Optional;

public interface PlageHoraireRepository extends JpaRepository<PlageHoraire, String> {
    @Query("""
    SELECT p FROM PlageHoraire p
    WHERE p.heureDebut <= :heure
    AND p.heureFin > :heure
    """)
    Optional<PlageHoraire> findByHeure(@Param("heure") LocalTime heure);

}