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
import com.pkrfc.rdv_backend.models.repositories.ServiceMetierRepository;
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
    private final ServiceMetierRepository serviceMetierRepository;


    @Override
    @Transactional
    public ResponsableResponse createOrUpdateResponsable(ResponsableRequest request) {

        ServiceMetier serviceMetier = serviceMetierRepository.findById(request.refService())
                .orElseThrow(() -> new ResourceNotFoundException("ServiceMetier", "ref", request.refService()));

        Responsable responsable;
        Utilisateur utilisateur;

        if (request.refResponsable() != null && !request.refResponsable().isBlank()) {
            Responsable existing = responsableRepository.findById(request.refResponsable())
                    .orElseThrow(() -> new ResourceNotFoundException("Responsable", "ref", request.refResponsable()));

            utilisateur = existing.getUtilisateur();
            if (!utilisateur.getEmail().equals(request.utilisateur().email())
                    && utilisateurRepository.existsByEmail(request.utilisateur().email())) {
                throw new DuplicateDataException("Responsable", "email", request.utilisateur().email());
            }

            UtilisateurMapper.updateEntity(utilisateur, request.utilisateur());
            utilisateurRepository.save(utilisateur);
            existing.setServiceMetier(serviceMetier);
            responsable = existing;

        } else {
            if (utilisateurRepository.existsByEmail(request.utilisateur().email())) {
                throw new DuplicateDataException("Responsable", "email", request.utilisateur().email());
            }
            utilisateur = UtilisateurMapper.toEntity(request.utilisateur());
            utilisateur = utilisateurRepository.save(utilisateur); // ← sauvegarder AVANT
            responsable = ResponsableMapper.toEntity(request, utilisateur, serviceMetier);
        }
        responsable = responsableRepository.save(responsable);
        log.info("Responsable sauvegardé : {}", responsable.getRefResponsable());
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

    @Transactional
    @Override
    public void deleteResponsable(String ref) {
        Responsable responsable = responsableRepository.findById(ref)
                .orElseThrow(() -> new ResourceNotFoundException("Responsable", "ref", ref));

        Utilisateur utilisateur = responsable.getUtilisateur();
        responsableRepository.delete(responsable);
        utilisateurRepository.delete(utilisateur);
    }
}