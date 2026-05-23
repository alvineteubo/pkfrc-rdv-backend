package com.pkrfc.rdv_backend.models.mappers;

import com.pkrfc.rdv_backend.models.dtos.requests.RendezVousRequest;
import com.pkrfc.rdv_backend.models.dtos.responses.ClientResponse;
import com.pkrfc.rdv_backend.models.dtos.responses.RendezVousResponse;
import com.pkrfc.rdv_backend.models.entities.*;
import com.pkrfc.rdv_backend.models.entities.enums.StatutRendezVous;

import java.util.List;

public class RendezVousMapper {

    public static RendezVous toEntity(RendezVousRequest request,
                                      Responsable responsable,
                                      Service service,
                                      PlageHoraire plageHoraire) {
        return RendezVous.builder()
                .responsable(responsable)
                .service(service)
                .plageHoraire(plageHoraire)
                .dateRdv(request.dateRdv())
                .motif(request.motif())
                .statut(StatutRendezVous.EN_ATTENTE)
                .build();
    }

    public static RendezVousResponse toResponse(RendezVous rendezVous,
                                                List<ClientResponse> participants) {
        return new RendezVousResponse(
                rendezVous.getRefRdv(),
                ResponsableMapper.toResponse(rendezVous.getResponsable()),
                ServiceMapper.toResponse(rendezVous.getService()),
                PlageHoraireMapper.toResponse(rendezVous.getPlageHoraire()),
                rendezVous.getDateRdv(),
                rendezVous.getMotif(),
                rendezVous.getStatut().name(),
                participants
        );
    }
}