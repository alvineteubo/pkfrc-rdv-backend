package com.pkrfc.rdv_backend.models.mappers;

import com.pkrfc.rdv_backend.models.dtos.requests.ResponsableRequest;
import com.pkrfc.rdv_backend.models.dtos.responses.ResponsableResponse;
import com.pkrfc.rdv_backend.models.entities.Responsable;
import com.pkrfc.rdv_backend.models.entities.Service;
import com.pkrfc.rdv_backend.models.entities.Utilisateur;

public class ResponsableMapper {

    public static Responsable toEntity(ResponsableRequest request, Utilisateur utilisateur, Service service) {
        return Responsable.builder()
                .utilisateur(utilisateur)
                .service(service)
                .build();
    }

    public static ResponsableResponse toResponse(Responsable responsable) {
        return new ResponsableResponse(
                responsable.getRefResponsable(),
                UtilisateurMapper.toResponse(responsable.getUtilisateur()),
                ServiceMapper.toResponse(responsable.getService())
        );
    }
}
