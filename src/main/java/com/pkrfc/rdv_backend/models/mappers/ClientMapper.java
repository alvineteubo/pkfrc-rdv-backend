package com.pkrfc.rdv_backend.models.mappers;


import com.pkrfc.rdv_backend.models.dtos.requests.ClientRequest;
import com.pkrfc.rdv_backend.models.dtos.responses.ClientResponse;
import com.pkrfc.rdv_backend.models.entities.Client;
import com.pkrfc.rdv_backend.models.entities.Utilisateur;

public class ClientMapper {

    public static Client toEntity(ClientRequest request, Utilisateur utilisateur) {
        return Client.builder()
                .utilisateur(utilisateur)
                .build();
    }

    public static ClientResponse toResponse(Client client) {
        return new ClientResponse(
                client.getRefClient(),
                UtilisateurMapper.toResponse(client.getUtilisateur())
        );
    }
}