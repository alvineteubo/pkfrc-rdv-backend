package com.pkrfc.rdv_backend.services.inter;

import com.pkrfc.rdv_backend.models.dtos.responses.ServiceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GestionServiceMetierService {
    ServiceResponse getServiceByRef(String ref);
    Page<ServiceResponse> getAllServices(String keyword, Pageable pageable);
}
