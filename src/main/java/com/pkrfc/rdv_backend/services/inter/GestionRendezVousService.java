package com.pkrfc.rdv_backend.services.inter;

import com.pkrfc.rdv_backend.models.dtos.requests.RendezVousRequest;
import com.pkrfc.rdv_backend.models.dtos.responses.RendezVousResponse;
import com.pkrfc.rdv_backend.models.entities.enums.StatutRendezVous;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface GestionRendezVousService {
    RendezVousResponse prendreRendezVous(RendezVousRequest request);
    RendezVousResponse getRendezVousByRef(String ref);
    Page<RendezVousResponse> getAllRendezVousByKeyword(String keyword, String refClient, String refResponsable, Pageable pageable);
    RendezVousResponse changerStatutRendezVous(String ref, StatutRendezVous nouveauStatut);
    RendezVousResponse ajouterParticipant(String refRdv, String refClient);
    RendezVousResponse retirerParticipant(String refRdv, String refClient);

}