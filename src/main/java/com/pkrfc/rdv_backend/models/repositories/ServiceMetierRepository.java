package com.pkrfc.rdv_backend.models.repositories;

import com.pkrfc.rdv_backend.models.entities.ServiceMetier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServiceMetierRepository extends JpaRepository<ServiceMetier, String> {


    @Query("""
    SELECT s FROM ServiceMetier s
    WHERE (:keyword IS NULL OR
           LOWER(s.nom) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')) OR
           LOWER(s.code) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
          )
    """)
    Page<ServiceMetier> getAllServicesByKeyword(@Param("keyword") String keyword, Pageable pageable);}