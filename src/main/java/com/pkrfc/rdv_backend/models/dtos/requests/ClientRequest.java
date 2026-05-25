package com.pkrfc.rdv_backend.models.dtos.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record ClientRequest(
        String refClient,

        @Valid
        @NotNull(message = "Les informations utilisateur sont obligatoires")
        UtilisateurRequest utilisateur) {
}
