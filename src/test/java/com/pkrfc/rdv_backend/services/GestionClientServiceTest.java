package com.pkrfc.rdv_backend.services;

import com.pkrfc.rdv_backend.exceptions.DuplicateDataException;
import com.pkrfc.rdv_backend.exceptions.ResourceNotFoundException;
import com.pkrfc.rdv_backend.models.dtos.requests.ClientRequest;
import com.pkrfc.rdv_backend.models.dtos.requests.UtilisateurRequest;
import com.pkrfc.rdv_backend.models.dtos.responses.ClientResponse;
import com.pkrfc.rdv_backend.models.entities.Client;
import com.pkrfc.rdv_backend.models.entities.Utilisateur;
import com.pkrfc.rdv_backend.models.repositories.ClientRepository;
import com.pkrfc.rdv_backend.models.repositories.UtilisateurRepository;
import com.pkrfc.rdv_backend.services.impl.GestionClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires — GestionClientService")
class GestionClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @InjectMocks
    private GestionClientServiceImpl clientService;

    private Utilisateur utilisateur;
    private Client client;
    private ClientRequest requestCreation;
    private ClientRequest requestModification;

    @BeforeEach
    void setUp() {
        utilisateur = Utilisateur.builder()
                .refUtilisateur("uuid-utilisateur-123")
                .nom("Kamga")
                .prenom("Paul")
                .email("paul.kamga@gmail.com")
                .telephone(699000001L)
                .build();

        client = Client.builder()
                .refClient("uuid-client-123")
                .utilisateur(utilisateur)
                .build();

        UtilisateurRequest utilisateurRequest = new UtilisateurRequest(
                null, "Kamga", "Paul", "paul.kamga@gmail.com", 699000001L
        );

        requestCreation = new ClientRequest(null, utilisateurRequest);

        requestModification = new ClientRequest(
                "uuid-client-123",
                new UtilisateurRequest(
                        null, "Kamga", "Paul", "paul.kamga.new@gmail.com", 699000001L
                )
        );
    }


    @Test
    @DisplayName("Créer un client avec succès")
    void creerClient_Success() {
        // GIVEN
        when(utilisateurRepository.existsByEmail(any())).thenReturn(false);
        when(utilisateurRepository.save(any())).thenReturn(utilisateur);
        when(clientRepository.save(any())).thenReturn(client);

        // WHEN
        ClientResponse response = clientService.createOrUpdateClient(requestCreation);

        // THEN
        assertNotNull(response);
        assertEquals("uuid-client-123", response.refClient());
        verify(utilisateurRepository, times(1)).save(any());
        verify(clientRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Créer un client — email déjà utilisé")
    void creerClient_EmailDuplique_LanceException() {
        // GIVEN
        when(utilisateurRepository.existsByEmail(any())).thenReturn(true);

        // WHEN & THEN
        assertThrows(DuplicateDataException.class, () ->
                clientService.createOrUpdateClient(requestCreation)
        );
        verify(clientRepository, never()).save(any());
        verify(utilisateurRepository, never()).save(any());
    }


    @Test
    @DisplayName("Modifier un client avec succès")
    void modifierClient_Success() {
        // GIVEN
        when(clientRepository.findById("uuid-client-123"))
                .thenReturn(Optional.of(client));
        when(utilisateurRepository.existsByEmail("paul.kamga.new@gmail.com"))
                .thenReturn(false);
        when(utilisateurRepository.save(any())).thenReturn(utilisateur);
        when(clientRepository.save(any())).thenReturn(client);

        // WHEN
        ClientResponse response = clientService.createOrUpdateClient(requestModification);

        // THEN
        assertNotNull(response);
        verify(utilisateurRepository, times(1)).save(any());
        verify(clientRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Modifier un client — ref introuvable")
    void modifierClient_RefIntrouvable_LanceException() {
        // GIVEN
        when(clientRepository.findById("bad-uuid"))
                .thenReturn(Optional.empty());

        ClientRequest requestBadRef = new ClientRequest(
                "bad-uuid",
                new UtilisateurRequest(null, "Kamga", "Paul", "paul@gmail.com", 699000001L)
        );

        // WHEN & THEN
        assertThrows(ResourceNotFoundException.class, () ->
                clientService.createOrUpdateClient(requestBadRef)
        );
    }

    @Test
    @DisplayName("Modifier un client — nouvel email déjà utilisé")
    void modifierClient_NouvelEmailDuplique_LanceException() {
        // GIVEN
        when(clientRepository.findById("uuid-client-123"))
                .thenReturn(Optional.of(client));
        when(utilisateurRepository.existsByEmail("paul.kamga.new@gmail.com"))
                .thenReturn(true);

        // WHEN & THEN
        assertThrows(DuplicateDataException.class, () ->
                clientService.createOrUpdateClient(requestModification)
        );
        verify(clientRepository, never()).save(any());
    }


    @Test
    @DisplayName("Consulter un client par ref — succès")
    void getClientByRef_Success() {
        // GIVEN
        when(clientRepository.findById("uuid-client-123"))
                .thenReturn(Optional.of(client));

        // WHEN
        ClientResponse response = clientService.getClientByRef("uuid-client-123");

        // THEN
        assertNotNull(response);
        assertEquals("uuid-client-123", response.refClient());
    }

    @Test
    @DisplayName("Consulter un client par ref — introuvable")
    void getClientByRef_NotFound_LanceException() {
        // GIVEN
        when(clientRepository.findById("bad-uuid"))
                .thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(ResourceNotFoundException.class, () ->
                clientService.getClientByRef("bad-uuid")
        );
    }


    @Test
    @DisplayName("Supprimer un client — succès")
    void supprimerClient_Success() {
        // GIVEN
        when(clientRepository.findById("uuid-client-123"))
                .thenReturn(Optional.of(client));

        // WHEN
        clientService.deleteClient("uuid-client-123");

        // THEN
        verify(clientRepository, times(1)).delete(client);
    }

    @Test
    @DisplayName("Supprimer un client — introuvable")
    void supprimerClient_NotFound_LanceException() {
        // GIVEN
        when(clientRepository.findById("bad-uuid"))
                .thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(ResourceNotFoundException.class, () ->
                clientService.deleteClient("bad-uuid")
        );
        verify(clientRepository, never()).delete(any());
    }
}
