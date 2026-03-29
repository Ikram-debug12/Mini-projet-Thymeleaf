package com.rh.conges.service;

import com.rh.conges.dto.DemandeCongeDTO;
import com.rh.conges.entity.DemandeConge;
import com.rh.conges.entity.Employe;
import com.rh.conges.entity.TypeConge;
import com.rh.conges.enums.StatutDemande;
import com.rh.conges.repository.DemandeCongeRepository;
import com.rh.conges.repository.EmployeRepository;
import com.rh.conges.repository.TypeCongeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DemandeCongeService {

    @Autowired
    private DemandeCongeRepository demandeCongeRepository;

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private TypeCongeRepository typeCongeRepository;

    // Soumettre une demande
    @Transactional
    public DemandeConge soumettreDemande(Long employeId, Long typeCongeId,
                                         LocalDate dateDebut, LocalDate dateFin,
                                         String motif) throws Exception {

        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new Exception("Employé non trouvé"));

        TypeConge typeConge = typeCongeRepository.findById(typeCongeId)
                .orElseThrow(() -> new Exception("Type de congé non trouvé"));

        // Vérifier les dates
        if (dateDebut.isAfter(dateFin)) {
            throw new Exception("La date de début doit être avant la date de fin");
        }

        // Vérifier le quota
        if (!verifierQuotaDisponible(employeId, typeCongeId, dateDebut, dateFin)) {
            throw new Exception("Quota insuffisant pour ce type de congé");
        }

        DemandeConge demande = new DemandeConge();
        demande.setEmploye(employe);
        demande.setTypeConge(typeConge);
        demande.setDateDebut(dateDebut);
        demande.setDateFin(dateFin);
        demande.setMotif(motif);
        demande.setStatut(StatutDemande.EN_ATTENTE);

        return demandeCongeRepository.save(demande);
    }

    // Valider une demande
    @Transactional
    public DemandeConge validerDemande(Long demandeId) throws Exception {
        DemandeConge demande = demandeCongeRepository.findById(demandeId)
                .orElseThrow(() -> new Exception("Demande non trouvée"));

        if (demande.getStatut() != StatutDemande.EN_ATTENTE) {
            throw new Exception("Seules les demandes en attente peuvent être validées");
        }

        demande.setStatut(StatutDemande.ACCEPTE);
        return demandeCongeRepository.save(demande);
    }

    // Rejeter une demande
    @Transactional
    public DemandeConge rejeterDemande(Long demandeId) throws Exception {
        DemandeConge demande = demandeCongeRepository.findById(demandeId)
                .orElseThrow(() -> new Exception("Demande non trouvée"));

        if (demande.getStatut() != StatutDemande.EN_ATTENTE) {
            throw new Exception("Seules les demandes en attente peuvent être rejetées");
        }

        demande.setStatut(StatutDemande.REFUSE);
        return demandeCongeRepository.save(demande);
    }

    // Vérifier le quota consommé
    public boolean verifierQuotaDisponible(Long employeId, Long typeCongeId,
                                           LocalDate dateDebut, LocalDate dateFin) {
        TypeConge typeConge = typeCongeRepository.findById(typeCongeId).orElse(null);
        if (typeConge == null) return false;

        List<DemandeConge> demandesAcceptees = demandeCongeRepository
                .findDemandesAcceptees(employeId, typeCongeId);

        long joursDejaConsommes = demandesAcceptees.stream()
                .mapToLong(DemandeConge::getNombreJours)
                .sum();

        long joursDemandes = java.time.temporal.ChronoUnit.DAYS.between(dateDebut, dateFin) + 1;

        return (joursDejaConsommes + joursDemandes) <= typeConge.getQuotaAnnuel();
    }

    // Obtenir le quota restant
    public long getQuotaRestant(Long employeId, Long typeCongeId) throws Exception {
        TypeConge typeConge = typeCongeRepository.findById(typeCongeId)
                .orElseThrow(() -> new Exception("Type de congé non trouvé"));

        List<DemandeConge> demandesAcceptees = demandeCongeRepository
                .findDemandesAcceptees(employeId, typeCongeId);

        long joursConsommes = demandesAcceptees.stream()
                .mapToLong(DemandeConge::getNombreJours)
                .sum();

        return typeConge.getQuotaAnnuel() - joursConsommes;
    }

    // Filtrage par statut
    public List<DemandeCongeDTO> filtrerParStatut(StatutDemande statut) {
        return demandeCongeRepository.findByStatut(statut).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Filtrage par département
    public List<DemandeCongeDTO> filtrerParDepartement(String departement) {
        return demandeCongeRepository.findByDepartement(departement).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Filtrage par période
    public List<DemandeCongeDTO> filtrerParPeriode(LocalDate debut, LocalDate fin) {
        return demandeCongeRepository.findByPeriode(debut, fin).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Filtrage par type de congé
    public List<DemandeCongeDTO> filtrerParTypeConge(Long typeCongeId) {
        return demandeCongeRepository.findByTypeCongeId(typeCongeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Obtenir toutes les demandes
    public List<DemandeCongeDTO> getAllDemandes() {
        return demandeCongeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Convertir Entity en DTO
    private DemandeCongeDTO convertToDTO(DemandeConge demande) {
        DemandeCongeDTO dto = new DemandeCongeDTO();
        dto.setId(demande.getId());
        dto.setDateDebut(demande.getDateDebut());
        dto.setDateFin(demande.getDateFin());
        dto.setMotif(demande.getMotif());
        dto.setStatut(demande.getStatut().toString());
        dto.setNombreJours(demande.getNombreJours());

        if (demande.getEmploye() != null) {
            dto.setEmployeId(demande.getEmploye().getId());
            dto.setEmployeNom(demande.getEmploye().getNom());
        }

        if (demande.getTypeConge() != null) {
            dto.setTypeCongeId(demande.getTypeConge().getId());
            dto.setTypeCongeLibelle(demande.getTypeConge().getLibelle());
        }

        return dto;
    }
}