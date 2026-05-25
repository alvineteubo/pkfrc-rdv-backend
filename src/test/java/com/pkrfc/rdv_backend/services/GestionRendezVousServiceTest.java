package com.pkrfc.rdv_backend.services;

import com.pkrfc.rdv_backend.exceptions.BadRequestException;
import com.pkrfc.rdv_backend.exceptions.ResourceNotFoundException;
import com.pkrfc.rdv_backend.models.dtos.requests.RendezVousRequest;
import com.pkrfc.rdv_backend.models.dtos.responses.RendezVousResponse;
import com.pkrfc.rdv_backend.models.entities.*;
import com.pkrfc.rdv_backend.models.entities.enums.StatutRendezVous;
import com.pkrfc.rdv_backend.models.repositories.*;
import com.pkrfc.rdv_backend.services.impl.GestionRendezVousServiceImpl;
import com.pkrfc.rdv_backend.utils.I18nUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires — GestionRendezVousService")
class GestionRendezVousServiceTest {

    @Mock
    private RendezVousRepository rendezVousRepository;

    @Mock
    private RendezVousParticipantRepository participantRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ResponsableRepository responsableRepository;

    @Mock
    private ServiceMetierRepository serviceRepository;

    @Mock
    private PlageHoraireRepository plageHoraireRepository;

    @InjectMocks
    private GestionRendezVousServiceImpl rendezVousService;

    private Utilisateur utilisateur;
    private Client client;
    private Responsable responsable;
    private ServiceMetier serviceMetier;
    private PlageHoraire plageHoraire;
    private RendezVous rendezVous;
    private RendezVousRequest request;

    // Date valide — J+3
    private final LocalDateTime dateValide = LocalDate.now()
            .plusDays(3)
            .atTime(9, 0);

    // Date invalide — J+1
    private final LocalDateTime dateTropProche = LocalDate.now()
            .plusDays(1)
            .atTime(9, 0);

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

        plageHoraire = PlageHoraire.builder()
                .idPlage("uuid-plage-123")
                .heureDebut(LocalTime.of(9, 0))
                .heureFin(LocalTime.of(10, 0))
                .build();

        rendezVous = RendezVous.builder()
                .refRdv("uuid-rdv-123")
                .responsable(responsable)
                .serviceMetier(serviceMetier)
                .plageHoraire(plageHoraire)
                .dateRdv(dateValide)
                .motif("Demande de document")
                .statut(StatutRendezVous.EN_ATTENTE)
                .participants(new ArrayList<>())
                .build();

        request = new RendezVousRequest(
                "uuid-client-123",
                "uuid-service-123",
                "uuid-responsable-123",
                dateValide,
                "Demande de document"
        );
    }

    // ===========================
    // ===== PRISE DE RDV =======
    // ===========================

    @Test
    @DisplayName("Prendre un RDV avec succès")
    void prendreRendezVous_Success() {
        // GIVEN
        when(clientRepository.findById("uuid-client-123"))
                .thenReturn(Optional.of(client));
        when(responsableRepository.findByIdWithLock("uuid-responsable-123"))
                .thenReturn(Optional.of(responsable));
        when(serviceRepository.findById("uuid-service-123"))
                .thenReturn(Optional.of(serviceMetier));
        when(plageHoraireRepository.findByHeureDebut(LocalTime.of(9, 0)))
                .thenReturn(Optional.of(plageHoraire));
        when(rendezVousRepository.findByResponsableAndPlageAndDate(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(rendezVousRepository.save(any())).thenReturn(rendezVous);
        when(participantRepository.save(any())).thenReturn(new RendezVousParticipant());

        // WHEN
        RendezVousResponse response = rendezVousService.prendreRendezVous(request);

        // THEN
        assertNotNull(response);
        assertEquals("uuid-rdv-123", response.refRendezVous());
        assertEquals("EN_ATTENTE", response.statut());
        verify(rendezVousRepository, times(1)).save(any());
        verify(participantRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Prendre un RDV — date trop proche")
    void prendreRendezVous_DateTropProche_LanceException() {
        // GIVEN
        RendezVousRequest requestDateInvalide = new RendezVousRequest(
                "uuid-client-123",
                "uuid-service-123",
                "uuid-responsable-123",
                dateTropProche,
                "Demande de document"
        );

        // WHEN & THEN
        assertThrows(BadRequestException.class, () ->
                rendezVousService.prendreRendezVous(requestDateInvalide)
        );
        verify(rendezVousRepository, never()).save(any());
    }

    @Test
    @DisplayName("Prendre un RDV — client introuvable")
    void prendreRendezVous_ClientIntrouvable_LanceException() {
        // GIVEN
        when(clientRepository.findById("bad-uuid"))
                .thenReturn(Optional.empty());

        RendezVousRequest requestBadClient = new RendezVousRequest(
                "bad-uuid",
                "uuid-service-123",
                "uuid-responsable-123",
                dateValide,
                "Demande de document"
        );

        // WHEN & THEN
        assertThrows(ResourceNotFoundException.class, () ->
                rendezVousService.prendreRendezVous(requestBadClient)
        );
        verify(rendezVousRepository, never()).save(any());
    }

    @Test
    @DisplayName("Prendre un RDV — plage horaire introuvable")
    void prendreRendezVous_PlageIntrouvable_LanceException() {
        // GIVEN
        when(clientRepository.findById("uuid-client-123"))
                .thenReturn(Optional.of(client));
        when(responsableRepository.findByIdWithLock("uuid-responsable-123"))
                .thenReturn(Optional.of(responsable));
        when(serviceRepository.findById("uuid-service-123"))
                .thenReturn(Optional.of(serviceMetier));
        when(plageHoraireRepository.findByHeureDebut(any()))
                .thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(BadRequestException.class, () ->
                rendezVousService.prendreRendezVous(request)
        );
        verify(rendezVousRepository, never()).save(any());
    }

    @Test
    @DisplayName("Prendre un RDV — responsable indisponible")
    void prendreRendezVous_ResponsableIndisponible_LanceException() {
        // GIVEN
        when(clientRepository.findById("uuid-client-123"))
                .thenReturn(Optional.of(client));
        when(responsableRepository.findByIdWithLock("uuid-responsable-123"))
                .thenReturn(Optional.of(responsable));
        when(serviceRepository.findById("uuid-service-123"))
                .thenReturn(Optional.of(serviceMetier));
        when(plageHoraireRepository.findByHeureDebut(any()))
                .thenReturn(Optional.of(plageHoraire));
        when(rendezVousRepository.findByResponsableAndPlageAndDate(any(), any(), any()))
                .thenReturn(List.of(rendezVous)); // ← RDV existant = indisponible

        // WHEN & THEN
        assertThrows(BadRequestException.class, () ->
                rendezVousService.prendreRendezVous(request)
        );
        verify(rendezVousRepository, never()).save(any());
    }

    // ===========================
    // ===== PARTICIPANTS ========
    // ===========================

    @Test
    @DisplayName("Ajouter un participant avec succès")
    void ajouterParticipant_Success() {
        // GIVEN
        Client client2 = Client.builder()
                .refClient("uuid-client-2")
                .utilisateur(utilisateur)
                .build();

        when(rendezVousRepository.findById("uuid-rdv-123"))
                .thenReturn(Optional.of(rendezVous));
        when(clientRepository.findById("uuid-client-2"))
                .thenReturn(Optional.of(client2));
        when(participantRepository.countByRendezVous(rendezVous)).thenReturn(1L);
        when(participantRepository.countByRendezVousAndRefClient(rendezVous, "uuid-client-2"))
                .thenReturn(0L);
        when(participantRepository.save(any())).thenReturn(new RendezVousParticipant());
        when(rendezVousRepository.findById("uuid-rdv-123"))
                .thenReturn(Optional.of(rendezVous));

        // WHEN
        RendezVousResponse response = rendezVousService
                .ajouterParticipant("uuid-rdv-123", "uuid-client-2");

        // THEN
        assertNotNull(response);
        verify(participantRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Ajouter un participant — max 2 participants atteint")
    void ajouterParticipant_MaxParticipants_LanceException() {
        // GIVEN
        when(rendezVousRepository.findById("uuid-rdv-123"))
                .thenReturn(Optional.of(rendezVous));
        when(clientRepository.findById("uuid-client-123"))
                .thenReturn(Optional.of(client));
        when(participantRepository.countByRendezVous(rendezVous)).thenReturn(2L); // ← déjà 2

        // WHEN & THEN
        assertThrows(BadRequestException.class, () ->
                rendezVousService.ajouterParticipant("uuid-rdv-123", "uuid-client-123")
        );
        verify(participantRepository, never()).save(any());
    }

    @Test
    @DisplayName("Ajouter un participant — client déjà inscrit")
    void ajouterParticipant_ClientDejaInscrit_LanceException() {
        // GIVEN
        when(rendezVousRepository.findById("uuid-rdv-123"))
                .thenReturn(Optional.of(rendezVous));
        when(clientRepository.findById("uuid-client-123"))
                .thenReturn(Optional.of(client));
        when(participantRepository.countByRendezVous(rendezVous)).thenReturn(1L);
        when(participantRepository.countByRendezVousAndRefClient(rendezVous, "uuid-client-123"))
                .thenReturn(1L); // ← déjà inscrit

        // WHEN & THEN
        assertThrows(BadRequestException.class, () ->
                rendezVousService.ajouterParticipant("uuid-rdv-123", "uuid-client-123")
        );
        verify(participantRepository, never()).save(any());
    }

    // ===========================
    // ===== STATUT =============
    // ===========================

    @Test
    @DisplayName("Changer statut EN_ATTENTE → CONFIRME avec succès")
    void changerStatut_EnAttenteVersConfirme_Success() {
        // GIVEN
        when(rendezVousRepository.findById("uuid-rdv-123"))
                .thenReturn(Optional.of(rendezVous));
        when(rendezVousRepository.save(any())).thenReturn(rendezVous);

        // WHEN
        RendezVousResponse response = rendezVousService
                .changerStatutRendezVous("uuid-rdv-123", StatutRendezVous.CONFIRME);

        // THEN
        assertNotNull(response);
        verify(rendezVousRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Changer statut — RDV déjà annulé")
    void changerStatut_DejaAnnule_LanceException() {
        // GIVEN
        rendezVous.setStatut(StatutRendezVous.ANNULE);
        when(rendezVousRepository.findById("uuid-rdv-123"))
                .thenReturn(Optional.of(rendezVous));

        // WHEN & THEN
        assertThrows(BadRequestException.class, () ->
                rendezVousService.changerStatutRendezVous(
                        "uuid-rdv-123", StatutRendezVous.ANNULE)
        );
        verify(rendezVousRepository, never()).save(any());
    }

    @Test
    @DisplayName("Changer statut — RDV déjà confirmé")
    void changerStatut_DejaConfirme_LanceException() {
        // GIVEN
        rendezVous.setStatut(StatutRendezVous.CONFIRME);
        when(rendezVousRepository.findById("uuid-rdv-123"))
                .thenReturn(Optional.of(rendezVous));

        // WHEN & THEN
        assertThrows(BadRequestException.class, () ->
                rendezVousService.changerStatutRendezVous(
                        "uuid-rdv-123", StatutRendezVous.CONFIRME)
        );
        verify(rendezVousRepository, never()).save(any());
    }

    @Test
    @DisplayName("Consulter un RDV par ref — succès")
    void getRendezVousByRef_Success() {
        // GIVEN
        when(rendezVousRepository.findById("uuid-rdv-123"))
                .thenReturn(Optional.of(rendezVous));

        // WHEN
        RendezVousResponse response = rendezVousService
                .getRendezVousByRef("uuid-rdv-123");

        // THEN
        assertNotNull(response);
        assertEquals("uuid-rdv-123", response.refRendezVous());
    }

    @Test
    @DisplayName("Consulter un RDV par ref — introuvable")
    void getRendezVousByRef_NotFound_LanceException() {
        // GIVEN
        when(rendezVousRepository.findById("bad-uuid"))
                .thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(ResourceNotFoundException.class, () ->
                rendezVousService.getRendezVousByRef("bad-uuid")
        );
    }
}
