package com.pkrfc.rdv_backend.services.impl;

import com.pkrfc.rdv_backend.exceptions.DuplicateDataException;
import com.pkrfc.rdv_backend.exceptions.ResourceNotFoundException;
import com.pkrfc.rdv_backend.models.dtos.requests.UtilisateurRequest;
import com.pkrfc.rdv_backend.models.entities.Utilisateur;
import com.pkrfc.rdv_backend.models.mappers.UtilisateurMapper;
import com.pkrfc.rdv_backend.models.repositories.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServiceHelper {

    private final UtilisateurRepository utilisateurRepository;

    public Utilisateur creerUtilisateur(UtilisateurRequest request) {
        if (utilisateurRepository.existsByEmail(request.email())) {
            throw new DuplicateDataException("Utilisateur", "email", request.email());
        }
        return utilisateurRepository.save(UtilisateurMapper.toEntity(request));
    }

    public Utilisateur mettreAJourUtilisateur(Utilisateur existant, UtilisateurRequest request) {
        if (!existant.getEmail().equals(request.email())
                && utilisateurRepository.existsByEmail(request.email())) {
            throw new DuplicateDataException("Utilisateur", "email", request.email());
        }
        UtilisateurMapper.updateEntity(existant, request);
        return utilisateurRepository.save(existant);
    }

    public Utilisateur chargerUtilisateur(String refUtilisateur) {
        return utilisateurRepository.findById(refUtilisateur)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "ref", refUtilisateur));
    }
}