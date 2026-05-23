package com.pkrfc.rdv_backend.models.mappers;

import com.pkrfc.rdv_backend.models.dtos.requests.ServiceRequest;
import com.pkrfc.rdv_backend.models.dtos.responses.ServiceResponse;
import com.pkrfc.rdv_backend.models.entities.Service;

public class ServiceMapper {
    public static Service toEntity (ServiceRequest serviceRequest) {
        return Service.builder()
                .code(serviceRequest.code())
                .nom(serviceRequest.nom())
                .description(serviceRequest.description())
                .build();
    }

    public static Service updateEntity (Service service, ServiceRequest serviceRequest) {
        service.setCode(serviceRequest.code());
        service.setNom(serviceRequest.nom());
        service.setDescription(serviceRequest.description());
        return service;
    }

    public static ServiceResponse toResponse (Service service) {
        return  new ServiceResponse(
                service.getRefService(),
                service.getCode(),
                service.getNom(),
                service.getDescription());
    }
}
