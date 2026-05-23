package com.pkrfc.rdv_backend.models.repositories;

import com.pkrfc.rdv_backend.models.entities.RendezVousParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RendezVousParticipantRepository extends JpaRepository<RendezVousParticipant, String> {
}