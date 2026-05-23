package com.pkrfc.rdv_backend.services.impl;

import com.pkrfc.rdv_backend.exceptions.DuplicateDataException;
import com.pkrfc.rdv_backend.exceptions.ResourceNotFoundException;
import com.pkrfc.rdv_backend.models.dtos.requests.UtilisateurRequest;
import com.pkrfc.rdv_backend.models.dtos.responses.UtilisateurResponse;
import com.pkrfc.rdv_backend.models.entities.Utilisateur;
import com.pkrfc.rdv_backend.models.mappers.UtilisateurMapper;
import com.pkrfc.rdv_backend.models.repositories.UtilisateurRepository;
import com.pkrfc.rdv_backend.services.inter.GestionUtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class GestionUtilisateurServiceImpl implements GestionUtilisateurService {
    private final UtilisateurRepository utilisateurRepository;


    @Override
    @Transactional
    public UtilisateurResponse createOrUpdateUtilisateur(UtilisateurRequest request) {

        Utilisateur utilisateur;

        if (request.refUtilisateur() != null) {
            Utilisateur existing = utilisateurRepository.findById(request.refUtilisateur())
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "ref", request.refUtilisateur()));

            if (!existing.getEmail().equals(request.email()) && utilisateurRepository.existsByEmail(request.email())) {
                throw new DuplicateDataException("Utilisateur", "email", request.email());
            }
            utilisateur = UtilisateurMapper.updateEntity(existing, request);

        } else {
            if (utilisateurRepository.existsByEmail(request.email())) {
                throw new DuplicateDataException("Utilisateur", "email", request.email());
            }

            utilisateur = UtilisateurMapper.toEntity(request);
        }
        return UtilisateurMapper.toResponse(utilisateurRepository.save(utilisateur));
    }

    @Override
    @Transactional(readOnly = true)
    public UtilisateurResponse getUtilisateurByRef(String refUtilisateur) {
        return UtilisateurMapper.toResponse(
                utilisateurRepository.findByRefUtilisateur(refUtilisateur)
                        .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "ref", refUtilisateur))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UtilisateurResponse getUtilisateurByEmail(String email) {
        return UtilisateurMapper.toResponse(utilisateurRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "email", email))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UtilisateurResponse> getAllUtilisateurByKeyword(String keyword, Pageable pageable) {
        return UtilisateurMapper.buildPageFromEntities(
                utilisateurRepository.getAllUtilisateursByKeyword(keyword, pageable)
        );
    }

    @Override
    @Transactional
    public void deleteUtilisateur(String ref) {
        Utilisateur utilisateur = utilisateurRepository.findById(ref)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "ref", ref));
        utilisateurRepository.delete(utilisateur);
    }
}