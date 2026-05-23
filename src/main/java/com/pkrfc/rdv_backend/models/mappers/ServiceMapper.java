package com.pkrfc.rdv_backend.models.mappers;

import com.pkrfc.rdv_backend.models.dtos.requests.ServiceRequest;
import com.pkrfc.rdv_backend.models.dtos.responses.ServiceResponse;
import com.pkrfc.rdv_backend.models.entities.ServiceMetier;
import org.springframework.data.domain.Page;

public class ServiceMapper {
    public static ServiceMetier toEntity (ServiceRequest serviceRequest) {
        return ServiceMetier.builder()
                .code(serviceRequest.code())
                .nom(serviceRequest.nom())
                .description(serviceRequest.description())
                .build();
    }

    public static ServiceMetier updateEntity (ServiceMetier serviceMetier, ServiceRequest serviceRequest) {
        serviceMetier.setCode(serviceRequest.code());
        serviceMetier.setNom(serviceRequest.nom());
        serviceMetier.setDescription(serviceRequest.description());
        return serviceMetier;
    }

    public static ServiceResponse toResponse (ServiceMetier serviceMetier) {
        return  new ServiceResponse(
                serviceMetier.getRefService(),
                serviceMetier.getCode(),
                serviceMetier.getNom(),
                serviceMetier.getDescription());
    }
    public static Page<ServiceResponse> buildPageFromEntities(Page<ServiceMetier> page) {
        return page.map(ServiceMapper::toResponse);
    }
}
