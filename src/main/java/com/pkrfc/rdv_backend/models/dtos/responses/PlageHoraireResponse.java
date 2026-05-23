package com.pkrfc.rdv_backend.models.dtos.responses;

import java.time.LocalTime;

public record PlageHoraireResponse(
        String idPlageHoraire,
        LocalTime heureDebut,
        LocalTime heureFin
) {}
