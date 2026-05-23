package com.pkrfc.rdv_backend.models.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rendez_vous_participant",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_rdv_client",
                        columnNames = {"ref_rendez_vous", "ref_client"}
                )
        }
)
public class RendezVousParticipant extends AbstractAudit {

    @Id
    @UuidGenerator
    @Column(name = "id_participant", updatable = false, nullable = false)
    private String idParticipant;

    @ManyToOne
    @JoinColumn(name = "ref_rendez_vous", nullable = false)
    private RendezVous rendezVous;

    @ManyToOne
    @JoinColumn(name = "ref_client", nullable = false)
    private Client client;
}
