package com.pkrfc.rdv_backend.models.repositories;

import com.pkrfc.rdv_backend.models.entities.ServiceMetier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ServiceRepository extends JpaRepository<ServiceMetier, String> {


    @Query("""
    SELECT s FROM ServiceMetier s
    WHERE (:keyword IS NULL OR
           LOWER(s.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
           LOWER(s.code) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
    """)
    Page<ServiceMetier> getAllServicesByKeyword(@Param("keyword") String keyword, Pageable pageable);
}