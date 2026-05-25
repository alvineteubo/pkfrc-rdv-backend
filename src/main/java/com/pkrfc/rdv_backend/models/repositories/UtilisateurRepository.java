package com.pkrfc.rdv_backend.models.repositories;

import com.pkrfc.rdv_backend.models.entities.Utilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, String> {
    boolean existsByEmail(String email);

    Optional<Utilisateur> findByEmail(String email);
    Optional<Utilisateur> findByRefUtilisateur (String refUtilisateur);

    @Query("""
    SELECT u FROM Utilisateur u
    WHERE (:keyword IS NULL OR
           LOWER(u.nom) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')) OR
           LOWER(u.prenom) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')) OR
           LOWER(u.email) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
         )
    """)
    Page<Utilisateur> getAllUtilisateursByKeyword(@Param("keyword") String keyword, Pageable pageable);
}