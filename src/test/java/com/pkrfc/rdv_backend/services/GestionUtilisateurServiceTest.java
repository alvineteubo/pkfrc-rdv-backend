package com.pkrfc.rdv_backend.services;

import com.pkrfc.rdv_backend.exceptions.DuplicateDataException;
import com.pkrfc.rdv_backend.exceptions.ResourceNotFoundException;
import com.pkrfc.rdv_backend.models.dtos.requests.UtilisateurRequest;
import com.pkrfc.rdv_backend.models.dtos.responses.UtilisateurResponse;
import com.pkrfc.rdv_backend.models.entities.Utilisateur;
import com.pkrfc.rdv_backend.models.repositories.UtilisateurRepository;
import com.pkrfc.rdv_backend.services.impl.GestionUtilisateurServiceImpl;
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
@DisplayName("Tests unitaires — GestionUtilisateurService")
class GestionUtilisateurServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @InjectMocks
    private GestionUtilisateurServiceImpl utilisateurService;

    private Utilisateur utilisateur;
    private UtilisateurRequest requestCreation;
    private UtilisateurRequest requestModification;

    @BeforeEach
    void setUp() {
        utilisateur = Utilisateur.builder()
                .refUtilisateur("uuid-utilisateur-123")
                .nom("Kamga")
                .prenom("Paul")
                .email("paul.kamga@gmail.com")
                .telephone(699000001L)
                .build();

        requestCreation = new UtilisateurRequest(
                null,
                "Kamga",
                "Paul",
                "paul.kamga@gmail.com",
                699000001L
        );

        requestModification = new UtilisateurRequest(
                "uuid-utilisateur-123",
                "Kamga",
                "Paul",
                "paul.kamga.new@gmail.com",
                699000001L
        );
    }

    @Test
    @DisplayName("Créer un utilisateur avec succès")
    void creerUtilisateur_Success() {
        // GIVEN
        when(utilisateurRepository.existsByEmail(any())).thenReturn(false);
        when(utilisateurRepository.save(any())).thenReturn(utilisateur);

        // WHEN
        UtilisateurResponse response = utilisateurService.createOrUpdateUtilisateur(requestCreation);

        // THEN
        assertNotNull(response);
        assertEquals("Kamga", response.nom());
        assertEquals("paul.kamga@gmail.com", response.email());
        verify(utilisateurRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Créer un utilisateur — email déjà utilisé")
    void creerUtilisateur_EmailDuplique_LanceException() {
        // GIVEN
        when(utilisateurRepository.existsByEmail(any())).thenReturn(true);

        // WHEN & THEN
        assertThrows(DuplicateDataException.class, () ->
                utilisateurService.createOrUpdateUtilisateur(requestCreation)
        );
        verify(utilisateurRepository, never()).save(any());
    }


    @Test
    @DisplayName("Modifier un utilisateur avec succès")
    void modifierUtilisateur_Success() {
        // GIVEN
        when(utilisateurRepository.findById("uuid-utilisateur-123"))
                .thenReturn(Optional.of(utilisateur));
        when(utilisateurRepository.existsByEmail("paul.kamga.new@gmail.com"))
                .thenReturn(false);
        when(utilisateurRepository.save(any())).thenReturn(utilisateur);

        // WHEN
        UtilisateurResponse response = utilisateurService
                .createOrUpdateUtilisateur(requestModification);

        // THEN
        assertNotNull(response);
        verify(utilisateurRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Modifier un utilisateur — ref introuvable")
    void modifierUtilisateur_RefIntrouvable_LanceException() {
        // GIVEN
        when(utilisateurRepository.findById("bad-uuid"))
                .thenReturn(Optional.empty());

        UtilisateurRequest requestBadRef = new UtilisateurRequest(
                "bad-uuid", "Kamga", "Paul", "paul@gmail.com", 699000001L
        );

        // WHEN & THEN
        assertThrows(ResourceNotFoundException.class, () ->
                utilisateurService.createOrUpdateUtilisateur(requestBadRef)
        );
    }

    @Test
    @DisplayName("Modifier un utilisateur — nouvel email déjà utilisé")
    void modifierUtilisateur_NouvelEmailDuplique_LanceException() {
        // GIVEN
        when(utilisateurRepository.findById("uuid-utilisateur-123"))
                .thenReturn(Optional.of(utilisateur));
        when(utilisateurRepository.existsByEmail("paul.kamga.new@gmail.com"))
                .thenReturn(true);

        // WHEN & THEN
        assertThrows(DuplicateDataException.class, () ->
                utilisateurService.createOrUpdateUtilisateur(requestModification)
        );
        verify(utilisateurRepository, never()).save(any());
    }


    @Test
    @DisplayName("Consulter un utilisateur par ref — succès")
    void getUtilisateurByRef_Success() {
        // GIVEN
        when(utilisateurRepository.findById("uuid-utilisateur-123"))
                .thenReturn(Optional.of(utilisateur));

        // WHEN
        UtilisateurResponse response = utilisateurService
                .getUtilisateurByRef("uuid-utilisateur-123");

        // THEN
        assertNotNull(response);
        assertEquals("uuid-utilisateur-123", response.refUtilisateur());
    }

    @Test
    @DisplayName("Consulter un utilisateur par ref — introuvable")
    void getUtilisateurByRef_NotFound_LanceException() {
        // GIVEN
        when(utilisateurRepository.findById("bad-uuid"))
                .thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(ResourceNotFoundException.class, () ->
                utilisateurService.getUtilisateurByRef("bad-uuid")
        );
    }

    @Test
    @DisplayName("Consulter un utilisateur par email — succès")
    void getUtilisateurByEmail_Success() {
        // GIVEN
        when(utilisateurRepository.findByEmail("paul.kamga@gmail.com"))
                .thenReturn(Optional.of(utilisateur));

        // WHEN
        UtilisateurResponse response = utilisateurService
                .getUtilisateurByEmail("paul.kamga@gmail.com");

        // THEN
        assertNotNull(response);
        assertEquals("paul.kamga@gmail.com", response.email());
    }

    @Test
    @DisplayName("Consulter un utilisateur par email — introuvable")
    void getUtilisateurByEmail_NotFound_LanceException() {
        // GIVEN
        when(utilisateurRepository.findByEmail("bad@gmail.com"))
                .thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(ResourceNotFoundException.class, () ->
                utilisateurService.getUtilisateurByEmail("bad@gmail.com")
        );
    }


    @Test
    @DisplayName("Supprimer un utilisateur — succès")
    void supprimerUtilisateur_Success() {
        // GIVEN
        when(utilisateurRepository.findById("uuid-utilisateur-123"))
                .thenReturn(Optional.of(utilisateur));

        // WHEN
        utilisateurService.deleteUtilisateur("uuid-utilisateur-123");

        // THEN
        verify(utilisateurRepository, times(1)).delete(utilisateur);
    }

    @Test
    @DisplayName("Supprimer un utilisateur — introuvable")
    void supprimerUtilisateur_NotFound_LanceException() {
        // GIVEN
        when(utilisateurRepository.findById("bad-uuid"))
                .thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(ResourceNotFoundException.class, () ->
                utilisateurService.deleteUtilisateur("bad-uuid")
        );
        verify(utilisateurRepository, never()).delete(any());
    }
}