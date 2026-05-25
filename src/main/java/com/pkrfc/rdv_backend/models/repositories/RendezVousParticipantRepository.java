package com.pkrfc.rdv_backend.models.repositories;

import com.pkrfc.rdv_backend.models.entities.RendezVous;
import com.pkrfc.rdv_backend.models.entities.RendezVousParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RendezVousParticipantRepository extends JpaRepository<RendezVousParticipant, String> {


    @Query("""
        SELECT COUNT(p) FROM RendezVousParticipant p
        WHERE p.rendezVous = :rendezVous
        """)
    long countByRendezVous(@Param("rendezVous") RendezVous rendezVous);

    @Query("""
        SELECT COUNT(p) FROM RendezVousParticipant p
        WHERE p.rendezVous = :rendezVous
        AND p.client.refClient = :refClient
        """)
    long countByRendezVousAndRefClient(
            @Param("rendezVous") RendezVous rendezVous,
            @Param("refClient") String refClient);

    @Query("""
        SELECT p FROM RendezVousParticipant p
        WHERE p.rendezVous = :rendezVous
        AND p.client.refClient = :refClient
        """)
    Optional<RendezVousParticipant> findByRendezVousAndRefClient(
            @Param("rendezVous") RendezVous rendezVous,
            @Param("refClient") String refClient);
}
