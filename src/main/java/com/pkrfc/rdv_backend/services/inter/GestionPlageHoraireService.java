package com.pkrfc.rdv_backend.services.inter;

import com.pkrfc.rdv_backend.models.dtos.responses.PlageHoraireResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GestionPlageHoraireService {
        PlageHoraireResponse getPlageHoraireById(String idPlage);

        Page<PlageHoraireResponse> getAllPlagesHoraires(Pageable pageable);
}
