package com.pkrfc.rdv_backend.models.dtos.requests;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RendezVousRequest(
        @NotBlank(message = "La référence client est obligatoire")
        @Schema(name = "refClient", example = "uuid-client", required = true)
        String refClient,

        @NotBlank(message = "La référence service est obligatoire")
        @Schema(name = "refService", example = "uuid-service", required = true)
        String refService,

        @NotBlank(message = "La référence responsable est obligatoire")
        @Schema(name = "refResponsable", example = "uuid-responsable", required = true)
        String refResponsable,

        @NotBlank(message = "La référence plage est obligatoire")
        @Schema(name = "refPlage", example = "uuid-plage", required = true)
        String refPlage,

        @NotNull(message = "La date du rendez-vous est obligatoire")
        @Schema(name = "dateRdv", example = "2026-05-25", required = true)
        LocalDate dateRdv,

        @NotBlank(message = "Le motif est obligatoire")
        @Schema(name = "motif", example = "Demande de document", required = true)
        String motif
) {}
