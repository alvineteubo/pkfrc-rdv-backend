package com.pkrfc.rdv_backend.models.dtos.responses;

public record UtilisateurResponse(
       String refUtilisateur,
        String nom,
        String prenom,
        String email,
        Long telephone
) {
}
