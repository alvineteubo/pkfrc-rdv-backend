package com.pkrfc.rdv_backend.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "UTILISATEUR")
public class Utilisateur extends AbstractAudit{

    @Id
    @UuidGenerator
    @Column(name = "refUtilisateur", updatable = false, nullable = false)
    private String refUtilisateur;

    @Column(name = "nom", nullable = false )
    private String nom;

    @Column(name = "prenom")
    private String prenom;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "telephone", length = 20)
    private Long telephone;

}
