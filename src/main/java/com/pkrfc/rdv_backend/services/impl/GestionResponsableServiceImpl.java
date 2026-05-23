package com.pkrfc.rdv_backend.services.impl;

import com.pkrfc.rdv_backend.exceptions.DuplicateDataException;
import com.pkrfc.rdv_backend.exceptions.ResourceNotFoundException;
import com.pkrfc.rdv_backend.models.dtos.requests.ResponsableRequest;
import com.pkrfc.rdv_backend.models.dtos.responses.ResponsableResponse;
import com.pkrfc.rdv_backend.models.entities.Responsable;
import com.pkrfc.rdv_backend.models.entities.ServiceMetier;
import com.pkrfc.rdv_backend.models.entities.Utilisateur;
import com.pkrfc.rdv_backend.models.mappers.ResponsableMapper;
import com.pkrfc.rdv_backend.models.mappers.UtilisateurMapper;
import com.pkrfc.rdv_backend.models.repositories.ResponsableRepository;
import com.pkrfc.rdv_backend.models.repositories.ServiceRepository;
import com.pkrfc.rdv_backend.models.repositories.UtilisateurRepository;
import com.pkrfc.rdv_backend.services.inter.GestionResponsableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GestionResponsableServiceImpl implements GestionResponsableService {

    private final ResponsableRepository responsableRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ServiceRepository serviceRepository;

    @Override
    @Transactional
    public ResponsableResponse createOrUpdateResponsable(ResponsableRequest request) {

        ServiceMetier serviceMetier = serviceRepository.findById(request.refService())
                .orElseThrow(() -> new ResourceNotFoundException("ServiceMetier", "ref", request.refService()));

        Responsable responsable;
        Utilisateur utilisateur;

        if (request.refResponsable() != null) {
            Responsable existing = responsableRepository.findById(request.refResponsable())
                    .orElseThrow(() -> new ResourceNotFoundException("Responsable", "ref", request.refResponsable()));

            utilisateur = existing.getUtilisateur();
            if (!utilisateur.getEmail().equals(request.utilisateur().email())
                    && utilisateurRepository.existsByEmail(request.utilisateur().email())) {
                throw new DuplicateDataException("Responsable", "email", request.utilisateur().email());
            }

            UtilisateurMapper.updateEntity(utilisateur, request.utilisateur());
            existing.setServiceMetier(serviceMetier);
            responsable = existing;

        } else {
            if (utilisateurRepository.existsByEmail(request.utilisateur().email())) {
                throw new DuplicateDataException("Responsable", "email", request.utilisateur().email());
            }

            utilisateur = UtilisateurMapper.toEntity(request.utilisateur());
            responsable = ResponsableMapper.toEntity(request, utilisateur, serviceMetier);
        }

        utilisateurRepository.save(utilisateur);
        responsable = responsableRepository.save(responsable);
        return ResponsableMapper.toResponse(responsable);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponsableResponse getResponsableByRef(String ref) {
        return ResponsableMapper.toResponse(
                responsableRepository.findById(ref)
                        .orElseThrow(() -> new ResourceNotFoundException("Responsable", "ref", ref))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResponsableResponse> getAllResponsablesByKeyword(String keyword, String refService, Pageable pageable) {
        return ResponsableMapper.buildPageFromEntities(
                responsableRepository.getAllResponsablesByKeyword(keyword, refService, pageable)
        );
    }

    @Override
    @Transactional
    public void supprimerResponsable(String ref) {
        Responsable responsable = responsableRepository.findById(ref)
                .orElseThrow(() -> new ResourceNotFoundException("Responsable", "ref", ref));
        responsableRepository.delete(responsable);
    }
}