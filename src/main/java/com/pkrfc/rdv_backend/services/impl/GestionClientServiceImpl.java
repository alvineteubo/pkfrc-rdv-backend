package com.pkrfc.rdv_backend.services.impl;

import com.pkrfc.rdv_backend.exceptions.ResourceNotFoundException;
import com.pkrfc.rdv_backend.models.dtos.requests.ClientRequest;
import com.pkrfc.rdv_backend.models.dtos.responses.ClientResponse;
import com.pkrfc.rdv_backend.models.entities.Client;
import com.pkrfc.rdv_backend.models.entities.Utilisateur;
import com.pkrfc.rdv_backend.models.mappers.ClientMapper;
import com.pkrfc.rdv_backend.models.repositories.ClientRepository;
import com.pkrfc.rdv_backend.models.repositories.ResponsableRepository;
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
    private final ResponsableRepository responsableRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ServiceHelper serviceHelper;

    @Override
    @Transactional
    public ClientResponse createOrUpdateClient(ClientRequest request) {
        Client client;

        if (request.refClient() != null && !request.refClient().isBlank()) {
            Client existing = clientRepository.findById(request.refClient())
                    .orElseThrow(() -> new ResourceNotFoundException("Client", "ref", request.refClient()));
            serviceHelper.mettreAJourUtilisateur(existing.getUtilisateur(), request.utilisateur());
            client = existing;
            log.info("Client mis à jour : {}", request.refClient());
        } else {
            Utilisateur utilisateur = serviceHelper.creerUtilisateur(request.utilisateur());
            client = ClientMapper.toEntity(request, utilisateur);
            log.info("Création client pour email : {}", request.utilisateur().email());
        }

        client = clientRepository.save(client);
        return ClientMapper.toResponse(client);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponse getClientByRef(String ref) {
        return ClientMapper.toResponse(
                clientRepository.findById(ref)
                        .orElseThrow(() -> new ResourceNotFoundException("Client", "ref", ref))
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

        Utilisateur utilisateur = client.getUtilisateur();
        clientRepository.delete(client);

        // Ne supprime l'utilisateur que s'il n'est pas également responsable
        if (!responsableRepository.existsByUtilisateur(utilisateur)) {
            utilisateurRepository.delete(utilisateur);
        }
        log.info("Client supprimé : {}", ref);
    }
}