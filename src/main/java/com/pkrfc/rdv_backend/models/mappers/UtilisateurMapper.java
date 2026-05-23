package com.pkrfc.rdv_backend.models.mappers;

import com.pkrfc.rdv_backend.models.dtos.requests.UtilisateurRequest;
import com.pkrfc.rdv_backend.models.dtos.responses.UtilisateurResponse;
import com.pkrfc.rdv_backend.models.entities.Utilisateur;

public class UtilisateurMapper {

    public  static Utilisateur toentity (UtilisateurRequest dtoRequest){
        return Utilisateur.builder()
                .nom(dtoRequest.nom())
                .prenom(dtoRequest.prenom())
                .email(dtoRequest.email())
                .telephone(dtoRequest.telephone())
                .build();
    }

    public static Utilisateur updateEntity (Utilisateur utilisateur, UtilisateurRequest dtoRequest){
        utilisateur.setNom(dtoRequest.nom());
        utilisateur.setPrenom(dtoRequest.prenom());
        utilisateur.setEmail(dtoRequest.email());
        utilisateur.setTelephone(dtoRequest.telephone());
        return utilisateur;
    }


    public static UtilisateurResponse toResponse (Utilisateur utilisateur){
        return new UtilisateurResponse(
                utilisateur.getRefUtilisateur(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                utilisateur.getTelephone()
        );
    }
}
