package com.pkrfc.rdv_backend.models.repositories;

import com.pkrfc.rdv_backend.models.entities.PlageHoraire;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlageHoraireRepository extends JpaRepository<PlageHoraire, String> {
}