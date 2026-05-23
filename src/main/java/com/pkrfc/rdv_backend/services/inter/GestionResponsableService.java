package com.pkrfc.rdv_backend.services.inter;

import com.pkrfc.rdv_backend.models.dtos.requests.ResponsableRequest;
import com.pkrfc.rdv_backend.models.dtos.responses.ResponsableResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GestionResponsableService {
    ResponsableResponse createOrUpdateResponsable(ResponsableRequest request);
    ResponsableResponse getResponsableByRef(String ref);
    Page<ResponsableResponse> getAllResponsablesByKeyword(String keyword, String refService, Pageable pageable);
    void supprimerResponsable(String ref);
}
