package com.pkrfc.rdv_backend.models.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UtilisateurRequest(

        String refUtilisateur,

        @NotBlank(message = "Le nom est obligatoire")
        @Schema(name = "nom", example = "DASSE", required = true)
        String nom,

        @Schema(name = "prenom", example = "Alvine")
        String prenom,

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "L'email n'est pas valide")
        @Schema(name = "email", example = "test@gmail.com")
        String email,

        @Schema(name = "telephone", example = "+237690000000")
        Long telephone
) {
}
