package com.pkrfc.rdv_backend.integrationTest;
import com.pkrfc.rdv_backend.exceptions.ResourceNotFoundException;
import com.pkrfc.rdv_backend.models.dtos.requests.RendezVousRequest;
import com.pkrfc.rdv_backend.models.dtos.responses.RendezVousResponse;
import com.pkrfc.rdv_backend.models.entities.*;
import com.pkrfc.rdv_backend.models.entities.enums.StatutRendezVous;
import com.pkrfc.rdv_backend.models.repositories.*;
import com.pkrfc.rdv_backend.services.inter.GestionRendezVousService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Tests d'intégration — RendezVous")
class RendezVousIntegrationTest {

    @Autowired
    private GestionRendezVousService rendezVousService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ResponsableRepository responsableRepository;

    @Autowired
    private ServiceMetierRepository serviceRepository;

    @Autowired
    private PlageHoraireRepository plageHoraireRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private RendezVousRepository rendezVousRepository;

    @Autowired
    private RendezVousParticipantRepository participantRepository;

    private static String refClient1;
    private static String refClient2;
    private static String refResponsable;
    private static String refService;
    private static String refRdv;

    private final LocalDateTime dateValide = LocalDate.now()
            .plusDays(3)
            .atTime(10, 0);

    @BeforeEach
    void setUp() {
        participantRepository.deleteAll();
        rendezVousRepository.deleteAll();
        clientRepository.deleteAll();
        responsableRepository.deleteAll();
        utilisateurRepository.deleteAll();

        refService = serviceRepository.findAll()
                .getFirst()
                .getRefService();

        plageHoraireRepository.findAll()
                .stream()
                .filter(p -> p.getHeureDebut().equals(LocalTime.of(10, 0)))
                .findFirst()
                .orElseThrow();

        Utilisateur u1 = utilisateurRepository.save(
                Utilisateur.builder()
                        .nom("Kamga").prenom("Paul")
                        .email("paul.kamga@test.com")
                        .telephone(699000001L)
                        .build()
        );
        Client c1 = clientRepository.save(
                Client.builder().utilisateur(u1).build()
        );
        refClient1 = c1.getRefClient();

        Utilisateur u2 = utilisateurRepository.save(
                Utilisateur.builder()
                        .nom("Dupont").prenom("Marie")
                        .email("marie.dupont@test.com")
                        .telephone(699000002L)
                        .build()
        );
        Client c2 = clientRepository.save(
                Client.builder().utilisateur(u2).build()
        );
        refClient2 = c2.getRefClient();

        // Créer utilisateur + responsable
        Utilisateur u3 = utilisateurRepository.save(
                Utilisateur.builder()
                        .nom("Talla").prenom("Jean")
                        .email("jean.talla@test.com")
                        .telephone(699000003L)
                        .build()
        );
        ServiceMetier service = serviceRepository.findById(refService).orElseThrow();
        Responsable r = responsableRepository.save(
                Responsable.builder()
                        .utilisateur(u3)
                        .serviceMetier(service)
                        .build()
        );
        refResponsable = r.getRefResponsable();
    }


    @Test
    @Order(1)
    @DisplayName("Flux complet — prise de RDV avec succès")
    void fluxComplet_PriseDeRdv_Success() {
        // GIVEN
        RendezVousRequest request = new RendezVousRequest(
                refClient1,
                refService,
                refResponsable,
                dateValide,
                "Demande de document"
        );

        // WHEN
        RendezVousResponse response = rendezVousService.prendreRendezVous(request);

        // THEN
        assertNotNull(response);
        assertNotNull(response.refRendezVous());
        assertEquals("EN_ATTENTE", response.statut());
        assertEquals(1, response.participants().size());
        assertEquals(refClient1, response.participants().getFirst().refClient());

        refRdv = response.refRendezVous();
    }

    @Test
    @Order(2)
    @DisplayName("Flux complet — ajouter participant puis changer statut")
    void fluxComplet_AjoutParticipant_EtChangerStatut() {
        // GIVEN —
        RendezVousRequest request = new RendezVousRequest(
                refClient1,
                refService,
                refResponsable,
                dateValide,
                "Demande de document"
        );
        RendezVousResponse rdv = rendezVousService.prendreRendezVous(request);
        String refRdvLocal = rdv.refRendezVous();

        // WHEN —
        RendezVousResponse apresAjout = rendezVousService
                .ajouterParticipant(refRdvLocal, refClient2);

        // THEN
        assertEquals(2, apresAjout.participants().size());

        // WHEN
        RendezVousResponse apresConfirmation = rendezVousService
                .changerStatutRendezVous(refRdvLocal, StatutRendezVous.CONFIRME);

        // THEN
        assertEquals("CONFIRME", apresConfirmation.statut());

        // WHEN
        RendezVousResponse apresAnnulation = rendezVousService
                .changerStatutRendezVous(refRdvLocal, StatutRendezVous.ANNULE);

        // THEN
        assertEquals("ANNULE", apresAnnulation.statut());
    }


    @Test
    @Order(3)
    @DisplayName("Rollback transactionnel — client inexistant, aucun RDV persisté")
    void prendreRendezVous_ClientInexistant_RollbackComplet() {
        // GIVEN
        RendezVousRequest request = new RendezVousRequest(
                "ref-client-inexistant",
                refService,
                refResponsable,
                dateValide,
                "Test rollback"
        );

        // WHEN & THEN
        assertThrows(ResourceNotFoundException.class, () ->
                rendezVousService.prendreRendezVous(request));

        assertEquals(0, rendezVousRepository.count(), "Aucun RDV ne doit être persisté après rollback");
        assertEquals(0, participantRepository.count(), "Aucun participant ne doit être persisté après rollback");
    }

    @Test
    @Order(4)
    @DisplayName("Concurrence — deux RDV simultanés sur la même plage")
    void fluxComplet_Concurrence_UnSeulSucces() throws InterruptedException {
        // GIVEN
        int nombreThreads = 2;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(nombreThreads);
        AtomicInteger succes = new AtomicInteger(0);
        AtomicInteger echecs = new AtomicInteger(0);

        RendezVousRequest request = new RendezVousRequest(
                refClient1,
                refService,
                refResponsable,
                dateValide,
                "Demande de document"
        );

        // WHEN
        ExecutorService executor = Executors.newFixedThreadPool(nombreThreads);

        for (int i = 0; i < nombreThreads; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    rendezVousService.prendreRendezVous(request);
                    succes.incrementAndGet();
                } catch (Exception e) {
                    echecs.incrementAndGet();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown(); // ← signal de départ simultan
        endLatch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        // THEN
        assertEquals(1, succes.get(), "Un seul RDV doit être créé");
        assertEquals(1, echecs.get(), "Un RDV doit être refusé");
        assertEquals(1, rendezVousRepository.count(), "Un seul RDV en BDD");
    }
}
