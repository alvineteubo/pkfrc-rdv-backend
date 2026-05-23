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
@Table(name = "service")
public class Service extends AbstractAudit {

    @Id
    @UuidGenerator
    @Column(name = "ref_service", updatable = false, nullable = false)
    private String refService;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "description", length = 255)
    private String description;
}
