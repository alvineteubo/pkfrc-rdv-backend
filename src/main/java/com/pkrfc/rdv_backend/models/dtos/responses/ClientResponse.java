package com.pkrfc.rdv_backend.models.dtos.responses;



public record ClientResponse(
        String refClient,
        UtilisateurResponse utilisateur
) {}
