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
@Table(name = "responsable")
public class Responsable extends AbstractAudit {

    @Id
    @UuidGenerator
    @Column(name = "ref_responsable", updatable = false, nullable = false)
    private String refResponsable;

    @OneToOne
    @JoinColumn(name = "ref_utilisateur", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "ref_service", nullable = false)
    private ServiceMetier serviceMetier;
}
