package com.pkrfc.rdv_backend.models.repositories;

import com.pkrfc.rdv_backend.models.entities.RendezVous;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RendezVousRepository extends JpaRepository<RendezVous, String> {
    @Query("""
    SELECT r FROM RendezVous r
    JOIN r.participants p
    WHERE (:keyword IS NULL OR
           LOWER(r.motif) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
    AND (:refClient IS NULL OR p.client.refClient = :refClient)
    AND (:refResponsable IS NULL OR r.responsable.refResponsable = :refResponsable)
    """)
    Page<RendezVous> getAllRendezVousByKeyword(
            @Param("keyword") String keyword,
            @Param("refClient") String refClient,
            @Param("refResponsable") String refResponsable,
            Pageable pageable);
}