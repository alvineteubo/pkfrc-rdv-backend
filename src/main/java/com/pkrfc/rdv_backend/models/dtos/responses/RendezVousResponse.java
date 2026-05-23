package com.pkrfc.rdv_backend.models.dtos.responses;


import java.time.LocalDateTime;
import java.util.List;

public record RendezVousResponse(
        String refRendezVous,
        ResponsableResponse responsable,
        ServiceResponse service,
        PlageHoraireResponse plageHoraire,
        LocalDateTime dateRdv,
        String motif,
        String statut,
        List<ClientResponse> participants
) {}
