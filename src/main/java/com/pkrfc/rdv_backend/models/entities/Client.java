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
@Table(name = "client")
public class Client extends AbstractAudit {

    @Id
    @UuidGenerator
    @Column(name = "ref_client", updatable = false, nullable = false)
    private String refClient;

    @OneToOne
    @JoinColumn(name = "ref_utilisateur", nullable = false)
    private Utilisateur utilisateur;
}
