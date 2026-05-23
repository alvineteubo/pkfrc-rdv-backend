package com.pkrfc.rdv_backend.models.entities;

import com.pkrfc.rdv_backend.models.entities.enums.StatutRendezVous;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rendez_vous",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_responsable_plage_date",
                        columnNames = {"ref_responsable", "id_plage", "date_rdv"}
                )
        }
)
public class RendezVous extends AbstractAudit {

    @Id
    @UuidGenerator
    @Column(name = "ref_rendez_vous", updatable = false, nullable = false)
    private String refRdv;

    @ManyToOne
    @JoinColumn(name = "ref_responsable", nullable = false)
    private Responsable responsable;

    @ManyToOne
    @JoinColumn(name = "ref_service", nullable = false)
    private ServiceMetier serviceMetier;

    @ManyToOne
    @JoinColumn(name = "id_plage", nullable = false)
    private PlageHoraire plageHoraire;

    @Column(name = "date_rdv", nullable = false)
    private LocalDateTime dateRdv;

    @Column(name = "motif", nullable = false, length = 500)
    private String motif;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutRendezVous statut ;

    @OneToMany(mappedBy = "rendezVous", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RendezVousParticipant> participants = new ArrayList<>();
}