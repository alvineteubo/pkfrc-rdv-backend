package com.pkrfc.rdv_backend.models.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ServiceRequest(

        @NotBlank(message = "Le code est obligatoire")
        @Schema(name = "code", example = "RH", required = true)
        String code,

        @NotBlank(message = "Le nom est obligatoire")
        @Schema(name = "nom", example = "Ressources Humaines", required = true)
        String nom,

        @Schema(name = "description", example = "Service des Ressources Humaines")
        String description
) {}


