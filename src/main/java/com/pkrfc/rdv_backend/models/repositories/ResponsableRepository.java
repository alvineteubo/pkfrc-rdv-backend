package com.pkrfc.rdv_backend.models.repositories;

import com.pkrfc.rdv_backend.models.entities.Responsable;
import com.pkrfc.rdv_backend.models.entities.Utilisateur;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ResponsableRepository extends JpaRepository<Responsable, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Responsable r WHERE r.refResponsable = :id")
    Optional<Responsable> findByIdWithLock(@Param("id") String id);

    boolean existsByUtilisateur(Utilisateur utilisateur);
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