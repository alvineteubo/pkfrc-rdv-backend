package com.pkrfc.rdv_backend.services.impl;

import com.pkrfc.rdv_backend.exceptions.DuplicateDataException;
import com.pkrfc.rdv_backend.exceptions.ResourceNotFoundException;
import com.pkrfc.rdv_backend.models.dtos.requests.ClientRequest;
import com.pkrfc.rdv_backend.models.dtos.responses.ClientResponse;
import com.pkrfc.rdv_backend.models.entities.Client;
import com.pkrfc.rdv_backend.models.entities.Utilisateur;
import com.pkrfc.rdv_backend.models.mappers.ClientMapper;
import com.pkrfc.rdv_backend.models.mappers.UtilisateurMapper;
import com.pkrfc.rdv_backend.models.repositories.ClientRepository;
import com.pkrfc.rdv_backend.models.repositories.UtilisateurRepository;
import com.pkrfc.rdv_backend.services.inter.GestionClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GestionClientServiceImpl implements GestionClientService {

    private final ClientRepository clientRepository;
    private final UtilisateurRepository utilisateurRepository;

    @Override
    @Transactional
    public ClientResponse createOrUpdateClient(ClientRequest request) {

        Client client;
        Utilisateur utilisateur;

        if (request.refClient() != null) {
            Client existing = clientRepository.findById(request.refClient())
                    .orElseThrow(() -> new ResourceNotFoundException("Client", "ref", request.refClient()));

            utilisateur = existing.getUtilisateur();
            if (!utilisateur.getEmail().equals(request.utilisateur().email()) && utilisateurRepository.existsByEmail(request.utilisateur().email())) {
                throw new DuplicateDataException("Client", "email", request.utilisateur().email());
            }

            UtilisateurMapper.updateEntity(utilisateur, request.utilisateur());
            client = existing;

        } else {
            if (utilisateurRepository.existsByEmail(request.utilisateur().email())) {
                throw new DuplicateDataException("Client", "email", request.utilisateur().email());
            }
            utilisateur = UtilisateurMapper.toEntity(request.utilisateur());
            client = ClientMapper.toEntity(request, utilisateur);
        }

        utilisateurRepository.save(utilisateur);
        client = clientRepository.save(client);
        log.info("Client sauvegardé : {}", client.getRefClient());
        return ClientMapper.toResponse(client);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponse getClientByRef(String ref) {
        return ClientMapper.toResponse(
                clientRepository.findById(ref).orElseThrow(() -> new ResourceNotFoundException("Client", "ref", ref))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientResponse> getAllClientsByKeyword(String keyword, Pageable pageable) {
        return ClientMapper.buildPageFromEntities(
                clientRepository.getAllClientsByKeyword(keyword, pageable)
        );
    }

    @Override
    @Transactional
    public void deleteClient(String ref) {
        Client client = clientRepository.findById(ref)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "ref", ref));
        clientRepository.delete(client);
    }
}
