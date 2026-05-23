package com.pkrfc.rdv_backend.services.impl;

import com.pkrfc.rdv_backend.exceptions.ResourceNotFoundException;
import com.pkrfc.rdv_backend.models.dtos.responses.ServiceResponse;
import com.pkrfc.rdv_backend.models.mappers.ServiceMapper;
import com.pkrfc.rdv_backend.models.repositories.ServiceRepository;
import com.pkrfc.rdv_backend.services.inter.GestionServiceMetierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GestionServiceMetierServiceImpl implements GestionServiceMetierService {

    private final ServiceRepository serviceRepository;

    @Override
    @Transactional(readOnly = true)
    public ServiceResponse getServiceByRef(String ref) {
        return ServiceMapper.toResponse(
                serviceRepository.findById(ref)
                        .orElseThrow(() -> new ResourceNotFoundException("ServiceMetier", "ref", ref))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceResponse> getAllServices(String keyword, Pageable pageable) {
        return ServiceMapper.buildPageFromEntities(
                serviceRepository.getAllServicesByKeyword(keyword, pageable)
        );
    }
}