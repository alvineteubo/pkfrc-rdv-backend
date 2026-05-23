package com.pkrfc.rdv_backend.models.mappers;

import com.pkrfc.rdv_backend.models.dtos.requests.RendezVousRequest;
import com.pkrfc.rdv_backend.models.dtos.responses.ClientResponse;
import com.pkrfc.rdv_backend.models.dtos.responses.RendezVousResponse;
import com.pkrfc.rdv_backend.models.entities.PlageHoraire;
import com.pkrfc.rdv_backend.models.entities.RendezVous;
import com.pkrfc.rdv_backend.models.entities.Responsable;
import com.pkrfc.rdv_backend.models.entities.ServiceMetier;
import com.pkrfc.rdv_backend.models.entities.enums.StatutRendezVous;
import org.springframework.data.domain.Page;

import java.util.List;

public class RendezVousMapper {

    public static RendezVous toEntity(RendezVousRequest request,
                                      Responsable responsable,
                                      ServiceMetier serviceMetier,
                                      PlageHoraire plageHoraire) {
        return RendezVous.builder()
                .responsable(responsable)
                .serviceMetier(serviceMetier)
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
                ServiceMapper.toResponse(rendezVous.getServiceMetier()),
                PlageHoraireMapper.toResponse(rendezVous.getPlageHoraire()),
                rendezVous.getDateRdv(),
                rendezVous.getMotif(),
                rendezVous.getStatut().name(),
                participants
        );
    }

    public static Page<RendezVousResponse> buildPageFromEntities(Page<RendezVous> page) {
        return page.map(rdv -> toResponse(rdv, rdv.getParticipants()
                .stream()
                .map(p -> ClientMapper.toResponse(p.getClient()))
                .toList()));
    }
}