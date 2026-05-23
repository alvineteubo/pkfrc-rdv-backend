package com.pkrfc.rdv_backend.models.entities;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "plage_horaire")
public class PlageHoraire extends AbstractAudit {

    @Id
    @UuidGenerator
    @Column(name = "id_plage_horaire", updatable = false, nullable = false)
    private String idPlage;

    @Column(name = "heure_debut", nullable = false)
    private LocalTime heureDebut;

    @Column(name = "heure_fin", nullable = false)
    private LocalTime heureFin;
}