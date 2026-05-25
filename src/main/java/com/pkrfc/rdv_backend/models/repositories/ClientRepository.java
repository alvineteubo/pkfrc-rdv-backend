package com.pkrfc.rdv_backend.models.repositories;

import com.pkrfc.rdv_backend.models.entities.Client;
import com.pkrfc.rdv_backend.models.entities.Utilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, String> {
    Optional<Client> findByUtilisateur_RefUtilisateur(String refUtilisateur);

    boolean existsByUtilisateur(Utilisateur utilisateur);

    @Query("""

            SELECT c FROM Client c
    WHERE (:keyword IS NULL OR
           LOWER(c.utilisateur.nom) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')) OR
           LOWER(c.utilisateur.prenom) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')) OR
           LOWER(c.utilisateur.email) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
          )
    """)
    Page<Client> getAllClientsByKeyword(@Param("keyword") String keyword, Pageable pageable);
    }