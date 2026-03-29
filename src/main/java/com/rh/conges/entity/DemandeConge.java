package com.rh.conges.entity;

import com.rh.conges.enums.StatutDemande;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Data
@Table(name = "demandes_conge")
public class DemandeConge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutDemande statut = StatutDemande.EN_ATTENTE;

    private String motif;

    @ManyToOne
    @JoinColumn(name = "employe_id", nullable = false)
    private Employe employe;

    @ManyToOne
    @JoinColumn(name = "type_conge_id", nullable = false)
    private TypeConge typeConge;

    // Calculer le nombre de jours
    public long getNombreJours() {
        return ChronoUnit.DAYS.between(dateDebut, dateFin) + 1;
    }
}
