package com.pkrfc.rdv_backend.models.mappers;

import com.pkrfc.rdv_backend.models.dtos.responses.PlageHoraireResponse;
import com.pkrfc.rdv_backend.models.entities.PlageHoraire;

public class PlageHoraireMapper {

    public static PlageHoraireResponse toResponse(PlageHoraire plageHoraire) {
        return new PlageHoraireResponse(
                plageHoraire.getIdPlage(),
                plageHoraire.getHeureDebut(),
                plageHoraire.getHeureFin()
        );
    }
}