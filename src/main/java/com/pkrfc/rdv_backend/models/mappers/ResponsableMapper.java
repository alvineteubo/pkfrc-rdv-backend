package com.pkrfc.rdv_backend.models.mappers;

import com.pkrfc.rdv_backend.models.dtos.requests.ResponsableRequest;
import com.pkrfc.rdv_backend.models.dtos.responses.ResponsableResponse;
import com.pkrfc.rdv_backend.models.entities.Responsable;
import com.pkrfc.rdv_backend.models.entities.ServiceMetier;
import com.pkrfc.rdv_backend.models.entities.Utilisateur;
import org.springframework.data.domain.Page;

public class ResponsableMapper {

    public static Responsable toEntity(ResponsableRequest request, Utilisateur utilisateur, ServiceMetier serviceMetier) {
        return Responsable.builder()
                .utilisateur(utilisateur)
                .serviceMetier(serviceMetier)
                .build();
    }

    public static ResponsableResponse toResponse(Responsable responsable) {
        return new ResponsableResponse(
                responsable.getRefResponsable(),
                UtilisateurMapper.toResponse(responsable.getUtilisateur()),
                ServiceMapper.toResponse(responsable.getServiceMetier())
        );
    }

    public static Page<ResponsableResponse> buildPageFromEntities(Page<Responsable> page) {
        return page.map(ResponsableMapper::toResponse);

    }
}