package com.rh.conges.entity;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "employes")
public class Employe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String departement;

    @Column(name = "date_embauche")
    private LocalDate dateEmbauche;

    @OneToMany(mappedBy = "employe", cascade = CascadeType.ALL)
    private List<DemandeConge> demandesConges = new ArrayList<>();
}