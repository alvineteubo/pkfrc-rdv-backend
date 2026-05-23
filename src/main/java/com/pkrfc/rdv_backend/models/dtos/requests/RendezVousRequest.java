package com.pkrfc.rdv_backend.models.dtos.requests;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RendezVousRequest(
        @NotBlank(message = "La référence client est obligatoire")
        @Schema(name = "refClient", example = "uuid-client", required = true)
        String refClient,

        @NotBlank(message = "La référence serviceMetier est obligatoire")
        @Schema(name = "refService", example = "uuid-serviceMetier", required = true)
        String refService,

        @NotBlank(message = "La référence responsable est obligatoire")
        @Schema(name = "refResponsable", example = "uuid-responsable", required = true)
        String refResponsable,

        @NotNull(message = "La date et l'heure du rendez-vous sont obligatoires")
        @Schema(name = "dateRdv", example = "2026-05-25T09:30:00", required = true)
        LocalDateTime dateRdv,

        @NotBlank(message = "Le motif est obligatoire")
        @Schema(name = "motif", example = "Demande de document", required = true)
        String motif
) {}
