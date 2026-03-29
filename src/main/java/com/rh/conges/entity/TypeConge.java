package com.rh.conges.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "types_conge")
public class TypeConge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String libelle;

    @Column(nullable = false)
    private Integer quotaAnnuel;

    @OneToMany(mappedBy = "typeConge", cascade = CascadeType.ALL)
    private List<DemandeConge> demandesConges = new ArrayList<>();
}