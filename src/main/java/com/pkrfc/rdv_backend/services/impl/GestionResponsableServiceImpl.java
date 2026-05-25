package com.pkrfc.rdv_backend.services.impl;

import com.pkrfc.rdv_backend.exceptions.ResourceNotFoundException;
import com.pkrfc.rdv_backend.models.dtos.requests.ResponsableRequest;
import com.pkrfc.rdv_backend.models.dtos.responses.ResponsableResponse;
import com.pkrfc.rdv_backend.models.entities.Responsable;
import com.pkrfc.rdv_backend.models.entities.ServiceMetier;
import com.pkrfc.rdv_backend.models.entities.Utilisateur;
import com.pkrfc.rdv_backend.models.mappers.ResponsableMapper;
import com.pkrfc.rdv_backend.models.repositories.ClientRepository;
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
    private final ClientRepository clientRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ServiceMetierRepository serviceMetierRepository;
    private final ServiceHelper serviceHelper;

    @Override
    @Transactional
    public ResponsableResponse createOrUpdateResponsable(ResponsableRequest request) {
        ServiceMetier serviceMetier = serviceMetierRepository.findById(request.refService())
                .orElseThrow(() -> new ResourceNotFoundException("ServiceMetier", "ref", request.refService()));

        Responsable responsable;

        if (request.refResponsable() != null && !request.refResponsable().isBlank()) {
            Responsable existing = responsableRepository.findById(request.refResponsable())
                    .orElseThrow(() -> new ResourceNotFoundException("Responsable", "ref", request.refResponsable()));
            serviceHelper.mettreAJourUtilisateur(existing.getUtilisateur(), request.utilisateur());
            existing.setServiceMetier(serviceMetier);
            responsable = existing;
            log.info("Responsable mis à jour : {}", request.refResponsable());
        } else {
            Utilisateur utilisateur = serviceHelper.creerUtilisateur(request.utilisateur());
            responsable = ResponsableMapper.toEntity(request, utilisateur, serviceMetier);
            log.info("Création responsable pour email : {}", request.utilisateur().email());
        }

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

    @Transactional
    @Override
    public void deleteResponsable(String ref) {
        Responsable responsable = responsableRepository.findById(ref)
                .orElseThrow(() -> new ResourceNotFoundException("Responsable", "ref", ref));

        Utilisateur utilisateur = responsable.getUtilisateur();
        responsableRepository.delete(responsable);

        // Ne supprime l'utilisateur que s'il n'est pas également client
        if (!clientRepository.existsByUtilisateur(utilisateur)) {
            utilisateurRepository.delete(utilisateur);
        }
        log.info("Responsable supprimé : {}", ref);
    }
}