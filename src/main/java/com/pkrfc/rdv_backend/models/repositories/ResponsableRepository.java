package com.pkrfc.rdv_backend.models.repositories;

import com.pkrfc.rdv_backend.models.entities.Responsable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ResponsableRepository extends JpaRepository<Responsable, String> {
    @Query("""
    SELECT r FROM Responsable r
    WHERE (:refService IS NULL OR r.serviceMetier.refService = CAST(:refService AS string))
    AND (:keyword IS NULL OR
         LOWER(r.utilisateur.nom) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')) OR
         LOWER(r.utilisateur.prenom) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')) OR
         LOWER(r.utilisateur.email) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
        )
    """)
    Page<Responsable> getAllResponsablesByKeyword(@Param("keyword") String keyword, @Param("refService") String refService, Pageable pageable);}