package com.rh.conges.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DemandeCongeDTO {
    private Long id;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String motif;
    private Long employeId;
    private String employeNom;
    private Long typeCongeId;
    private String typeCongeLibelle;
    private String statut;
    private Long nombreJours;

    // Ajoutez ce champ
    private String employeDepartement;

    // Getter et Setter
    public String getEmployeDepartement() { return employeDepartement; }
    public void setEmployeDepartement(String employeDepartement) { this.employeDepartement = employeDepartement; }
}