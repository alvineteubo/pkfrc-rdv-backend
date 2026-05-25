package com.pkrfc.rdv_backend.services.impl;

import com.pkrfc.rdv_backend.exceptions.ResourceNotFoundException;
import com.pkrfc.rdv_backend.models.dtos.responses.PlageHoraireResponse;
import com.pkrfc.rdv_backend.models.mappers.PlageHoraireMapper;
import com.pkrfc.rdv_backend.models.repositories.PlageHoraireRepository;
import com.pkrfc.rdv_backend.services.inter.GestionPlageHoraireService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GestionPlageHoraireServiceImpl implements GestionPlageHoraireService {

    private final PlageHoraireRepository plageHoraireRepository;

    @Transactional(readOnly = true)
    @Override
    public PlageHoraireResponse getPlageHoraireById(String idPlage) {
        return PlageHoraireMapper.toResponse(
                plageHoraireRepository.findById(idPlage)
                        .orElseThrow(() -> new ResourceNotFoundException("PlageHoraire", "id", idPlage))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PlageHoraireResponse> getAllPlagesHoraires(Pageable pageable) {
        Pageable pageableWithoutSort = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize()
        );
        return PlageHoraireMapper.buildPageFromEntities(
                plageHoraireRepository.findAllOrderByHeureDebutAsc(pageableWithoutSort)
        );
    }
}