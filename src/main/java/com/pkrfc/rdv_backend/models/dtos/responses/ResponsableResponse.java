package com.pkrfc.rdv_backend.models.dtos.responses;

public record ResponsableResponse(
        String refResponsable,
        UtilisateurResponse utilisateur,
        ServiceResponse service
) {}