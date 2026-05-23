package com.pkrfc.rdv_backend.models.dtos.responses;


import com.pkrfc.rdv_backend.models.entities.Client;

import java.time.LocalDate;
import java.util.List;

public record RendezVousResponse(
        String refRendezVous,
        ResponsableResponse responsable,
        ServiceResponse service,
        PlageHoraireResponse plageHoraire,
        LocalDate dateRdv,
        String motif,
        String statut,
        List<ClientResponse> participants
) {}
