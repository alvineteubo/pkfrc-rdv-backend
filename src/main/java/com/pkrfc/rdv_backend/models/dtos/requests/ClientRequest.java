package com.pkrfc.rdv_backend.models.dtos.requests;

public record ClientRequest(
        String refClient,
        UtilisateurRequest utilisateur) {
}
