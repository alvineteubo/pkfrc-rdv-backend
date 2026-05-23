package com.pkrfc.rdv_backend.models.mappers;

import com.pkrfc.rdv_backend.models.dtos.responses.PlageHoraireResponse;
import com.pkrfc.rdv_backend.models.entities.PlageHoraire;
import org.springframework.data.domain.Page;

public class PlageHoraireMapper {

    public static PlageHoraireResponse toResponse(PlageHoraire plageHoraire) {
        return new PlageHoraireResponse(
                plageHoraire.getIdPlage(),
                plageHoraire.getHeureDebut(),
                plageHoraire.getHeureFin()
        );
    }

    public static Page<PlageHoraireResponse> buildPageFromEntities(Page<PlageHoraire> page) {
        return page.map(PlageHoraireMapper::toResponse);
    }
}