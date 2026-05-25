package com.pkrfc.rdv_backend.models.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResponsableRequest(
        String refResponsable,

        @Valid
        @NotNull(message = "Les informations utilisateur sont obligatoires")
        @Schema(name = "utilisateur", required = true)
        UtilisateurRequest utilisateur,

        @NotBlank(message = "La référence du serviceMetier est obligatoire")
        @Schema(name = "refService", example = "uuid-du-serviceMetier", required = true)
        String refService
) {}
