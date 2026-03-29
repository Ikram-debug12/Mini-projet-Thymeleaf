package com.rh.conges.service;

import com.rh.conges.entity.DemandeConge;
import com.rh.conges.enums.StatutDemande;
import com.rh.conges.repository.DemandeCongeRepository;
import com.rh.conges.repository.EmployeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatistiqueService {

    @Autowired
    private DemandeCongeRepository demandeCongeRepository;

    @Autowired
    private EmployeRepository employeRepository;

    // Jours consommés par département
    public Map<String, Long> getJoursConsommesParDepartement() {
        List<DemandeConge> demandesAcceptees = demandeCongeRepository.findByStatut(StatutDemande.ACCEPTE);

        return demandesAcceptees.stream()
                .collect(Collectors.groupingBy(
                        d -> d.getEmploye().getDepartement(),
                        Collectors.summingLong(DemandeConge::getNombreJours)
                ));
    }

    // Taux d'acceptation global
    public double getTauxAcceptationGlobal() {
        List<DemandeConge> toutesDemandes = demandeCongeRepository.findAll();
        if (toutesDemandes.isEmpty()) return 0.0;

        long acceptees = toutesDemandes.stream()
                .filter(d -> d.getStatut() == StatutDemande.ACCEPTE)
                .count();

        return (double) acceptees / toutesDemandes.size() * 100;
    }

    // Taux d'acceptation par département
    public Map<String, Double> getTauxAcceptationParDepartement() {
        List<DemandeConge> toutesDemandes = demandeCongeRepository.findAll();

        Map<String, Long> totalParDept = toutesDemandes.stream()
                .collect(Collectors.groupingBy(
                        d -> d.getEmploye().getDepartement(),
                        Collectors.counting()
                ));

        Map<String, Long> accepteesParDept = toutesDemandes.stream()
                .filter(d -> d.getStatut() == StatutDemande.ACCEPTE)
                .collect(Collectors.groupingBy(
                        d -> d.getEmploye().getDepartement(),
                        Collectors.counting()
                ));

        Map<String, Double> tauxParDept = new HashMap<>();
        for (String dept : totalParDept.keySet()) {
            long total = totalParDept.get(dept);
            long acceptees = accepteesParDept.getOrDefault(dept, 0L);
            tauxParDept.put(dept, (double) acceptees / total * 100);
        }

        return tauxParDept;
    }

    // Résumé complet des statistiques
    public Map<String, Object> getStatistiquesCompletes() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("joursConsommesParDepartement", getJoursConsommesParDepartement());
        stats.put("tauxAcceptationGlobal", getTauxAcceptationGlobal());
        stats.put("tauxAcceptationParDepartement", getTauxAcceptationParDepartement());
        stats.put("nombreTotalDemandes", demandeCongeRepository.count());
        stats.put("nombreDemandesAcceptees", demandeCongeRepository.findByStatut(StatutDemande.ACCEPTE).size());
        stats.put("nombreDemandesRefusees", demandeCongeRepository.findByStatut(StatutDemande.REFUSE).size());
        stats.put("nombreDemandesEnAttente", demandeCongeRepository.findByStatut(StatutDemande.EN_ATTENTE).size());

        return stats;
    }
}
