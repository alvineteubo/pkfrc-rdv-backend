package com.pkrfc.rdv_backend.models.repositories;

import com.pkrfc.rdv_backend.models.entities.PlageHoraire;
import com.pkrfc.rdv_backend.models.entities.RendezVous;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RendezVousRepository extends JpaRepository<RendezVous, String> {
    @Query("""
    SELECT DISTINCT r FROM RendezVous r
    LEFT JOIN RendezVousParticipant p ON p.rendezVous = r
    WHERE (:keyword IS NULL OR
           LOWER(r.motif) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
          )
    AND (:refClient IS NULL OR p.client.refClient = CAST(:refClient AS string))
    AND (:refResponsable IS NULL OR r.responsable.refResponsable = CAST(:refResponsable AS string))
    """)
    Page<RendezVous> getAllRendezVousByKeyword(@Param("keyword") String keyword, @Param("refClient") String refClient, @Param("refResponsable") String refResponsable, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT r FROM RendezVous r
    WHERE r.responsable.refResponsable = :refResponsable
    AND r.plageHoraire = :plageHoraire
    AND CAST(r.dateRdv AS date) = CAST(:dateRdv AS date)
    """)
    List<RendezVous> findByResponsableAndPlageAndDate(
            @Param("refResponsable") String refResponsable,
            @Param("plageHoraire") PlageHoraire plageHoraire,
            @Param("dateRdv") LocalDateTime dateRdv);

    @Query("""
    SELECT COUNT(p) FROM RendezVousParticipant p
    WHERE p.rendezVous = :rendezVous
    """)
    long countParticipants(@Param("rendezVous") RendezVous rendezVous);

}

