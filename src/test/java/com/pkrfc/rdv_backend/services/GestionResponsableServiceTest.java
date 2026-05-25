package com.pkrfc.rdv_backend.services;

import com.pkrfc.rdv_backend.exceptions.DuplicateDataException;
import com.pkrfc.rdv_backend.exceptions.ResourceNotFoundException;
import com.pkrfc.rdv_backend.models.dtos.requests.ResponsableRequest;
import com.pkrfc.rdv_backend.models.dtos.requests.UtilisateurRequest;
import com.pkrfc.rdv_backend.models.dtos.responses.ResponsableResponse;
import com.pkrfc.rdv_backend.models.entities.Responsable;
import com.pkrfc.rdv_backend.models.entities.ServiceMetier;
import com.pkrfc.rdv_backend.models.entities.Utilisateur;
import com.pkrfc.rdv_backend.models.repositories.ClientRepository;
import com.pkrfc.rdv_backend.models.repositories.ResponsableRepository;
import com.pkrfc.rdv_backend.models.repositories.ServiceMetierRepository;
import com.pkrfc.rdv_backend.models.repositories.UtilisateurRepository;
import com.pkrfc.rdv_backend.services.impl.GestionResponsableServiceImpl;
import com.pkrfc.rdv_backend.services.impl.ServiceHelper;
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
@DisplayName("Tests unitaires — GestionResponsableService")
class GestionResponsableServiceTest {

    @Mock
    private ResponsableRepository responsableRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private ServiceMetierRepository serviceRepository;

    @Mock
    private ServiceHelper serviceHelper;

    @InjectMocks
    private GestionResponsableServiceImpl responsableService;

    private Utilisateur utilisateur;
    private ServiceMetier serviceMetier;
    private Responsable responsable;
    private ResponsableRequest requestCreation;
    private ResponsableRequest requestModification;

    @BeforeEach
    void setUp() {
        utilisateur = Utilisateur.builder()
                .refUtilisateur("uuid-utilisateur-123")
                .nom("Kamga")
                .prenom("Paul")
                .email("paul.kamga@gmail.com")
                .telephone(699000001L)
                .build();

        serviceMetier = ServiceMetier.builder()
                .refService("uuid-service-123")
                .code("RH")
                .nom("Ressources Humaines")
                .build();

        responsable = Responsable.builder()
                .refResponsable("uuid-responsable-123")
                .utilisateur(utilisateur)
                .serviceMetier(serviceMetier)
                .build();

        UtilisateurRequest utilisateurRequest = new UtilisateurRequest(
                null, "Kamga", "Paul", "paul.kamga@gmail.com", 699000001L
        );

        requestCreation = new ResponsableRequest(
                null,
                utilisateurRequest,
                "uuid-service-123"
        );

        requestModification = new ResponsableRequest(
                "uuid-responsable-123",
                new UtilisateurRequest(
                        null, "Kamga", "Paul", "paul.kamga.new@gmail.com", 699000001L
                ),
                "uuid-service-123"
        );
    }


    @Test
    @DisplayName("Créer un responsable avec succès")
    void creerResponsable_Success() {
        // GIVEN
        when(serviceRepository.findById("uuid-service-123"))
                .thenReturn(Optional.of(serviceMetier));
        when(serviceHelper.creerUtilisateur(any())).thenReturn(utilisateur);
        when(responsableRepository.save(any())).thenReturn(responsable);

        // WHEN
        ResponsableResponse response = responsableService
                .createOrUpdateResponsable(requestCreation);

        // THEN
        assertNotNull(response);
        assertEquals("uuid-responsable-123", response.refResponsable());
        assertEquals("RH", response.service().code());
        verify(serviceHelper, times(1)).creerUtilisateur(any());
        verify(responsableRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Créer un responsable — service introuvable")
    void creerResponsable_ServiceIntrouvable_LanceException() {
        // GIVEN
        when(serviceRepository.findById("bad-uuid"))
                .thenReturn(Optional.empty());

        ResponsableRequest requestBadService = new ResponsableRequest(
                null,
                new UtilisateurRequest(null, "Kamga", "Paul", "paul@gmail.com", 699000001L),
                "bad-uuid"
        );

        // WHEN & THEN
        assertThrows(ResourceNotFoundException.class, () ->
                responsableService.createOrUpdateResponsable(requestBadService)
        );
        verify(responsableRepository, never()).save(any());
    }

    @Test
    @DisplayName("Créer un responsable — email déjà utilisé")
    void creerResponsable_EmailDuplique_LanceException() {
        // GIVEN
        when(serviceRepository.findById("uuid-service-123"))
                .thenReturn(Optional.of(serviceMetier));
        when(serviceHelper.creerUtilisateur(any()))
                .thenThrow(new DuplicateDataException("Utilisateur", "email", "paul.kamga@gmail.com"));

        // WHEN & THEN
        assertThrows(DuplicateDataException.class, () ->
                responsableService.createOrUpdateResponsable(requestCreation)
        );
        verify(responsableRepository, never()).save(any());
    }

    @Test
    @DisplayName("Modifier un responsable avec succès")
    void modifierResponsable_Success() {
        // GIVEN
        when(serviceRepository.findById("uuid-service-123"))
                .thenReturn(Optional.of(serviceMetier));
        when(responsableRepository.findById("uuid-responsable-123"))
                .thenReturn(Optional.of(responsable));
        when(serviceHelper.mettreAJourUtilisateur(any(), any())).thenReturn(utilisateur);
        when(responsableRepository.save(any())).thenReturn(responsable);

        // WHEN
        ResponsableResponse response = responsableService
                .createOrUpdateResponsable(requestModification);

        // THEN
        assertNotNull(response);
        verify(serviceHelper, times(1)).mettreAJourUtilisateur(any(), any());
        verify(responsableRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Modifier un responsable — ref introuvable")
    void modifierResponsable_RefIntrouvable_LanceException() {
        // GIVEN
        when(serviceRepository.findById("uuid-service-123"))
                .thenReturn(Optional.of(serviceMetier));
        when(responsableRepository.findById("bad-uuid"))
                .thenReturn(Optional.empty());

        ResponsableRequest requestBadRef = new ResponsableRequest(
                "bad-uuid",
                new UtilisateurRequest(null, "Kamga", "Paul", "paul@gmail.com", 699000001L),
                "uuid-service-123"
        );

        // WHEN & THEN
        assertThrows(ResourceNotFoundException.class, () ->
                responsableService.createOrUpdateResponsable(requestBadRef)
        );
    }

    @Test
    @DisplayName("Modifier un responsable — nouvel email déjà utilisé")
    void modifierResponsable_NouvelEmailDuplique_LanceException() {
        // GIVEN
        when(serviceRepository.findById("uuid-service-123"))
                .thenReturn(Optional.of(serviceMetier));
        when(responsableRepository.findById("uuid-responsable-123"))
                .thenReturn(Optional.of(responsable));
        when(serviceHelper.mettreAJourUtilisateur(any(), any()))
                .thenThrow(new DuplicateDataException("Utilisateur", "email", "paul.kamga.new@gmail.com"));

        // WHEN & THEN
        assertThrows(DuplicateDataException.class, () ->
                responsableService.createOrUpdateResponsable(requestModification)
        );
        verify(responsableRepository, never()).save(any());
    }


    @Test
    @DisplayName("Consulter un responsable par ref — succès")
    void getResponsableByRef_Success() {
        // GIVEN
        when(responsableRepository.findById("uuid-responsable-123"))
                .thenReturn(Optional.of(responsable));

        // WHEN
        ResponsableResponse response = responsableService
                .getResponsableByRef("uuid-responsable-123");

        // THEN
        assertNotNull(response);
        assertEquals("uuid-responsable-123", response.refResponsable());
        assertEquals("paul.kamga@gmail.com", response.utilisateur().email());
    }

    @Test
    @DisplayName("Consulter un responsable par ref — introuvable")
    void getResponsableByRef_NotFound_LanceException() {
        // GIVEN
        when(responsableRepository.findById("bad-uuid"))
                .thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(ResourceNotFoundException.class, () ->
                responsableService.getResponsableByRef("bad-uuid")
        );
    }


    @Test
    @DisplayName("Supprimer un responsable — succès")
    void supprimerResponsable_Success() {
        // GIVEN
        when(responsableRepository.findById("uuid-responsable-123"))
                .thenReturn(Optional.of(responsable));
        when(clientRepository.existsByUtilisateur(utilisateur)).thenReturn(false);

        // WHEN
        responsableService.deleteResponsable("uuid-responsable-123");

        // THEN
        verify(responsableRepository, times(1)).delete(responsable);
        verify(utilisateurRepository, times(1)).delete(utilisateur);
    }

    @Test
    @DisplayName("Supprimer un responsable — introuvable")
    void supprimerResponsable_NotFound_LanceException() {
        // GIVEN
        when(responsableRepository.findById("bad-uuid"))
                .thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(ResourceNotFoundException.class, () ->
                responsableService.deleteResponsable("bad-uuid")
        );
        verify(responsableRepository, never()).delete(any());
        verify(utilisateurRepository, never()).delete(any());
    }
}