    package com.rh.conges.controller;

import com.rh.conges.dto.DemandeCongeDTO;
import com.rh.conges.entity.Employe;
import com.rh.conges.entity.TypeConge;
import com.rh.conges.enums.StatutDemande;
import com.rh.conges.repository.EmployeRepository;
import com.rh.conges.repository.TypeCongeRepository;
import com.rh.conges.service.DemandeCongeService;
import com.rh.conges.service.StatistiqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class CongeWebController {

    @Autowired
    private DemandeCongeService demandeCongeService;

    @Autowired
    private StatistiqueService statistiqueService;

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private TypeCongeRepository typeCongeRepository;

    // ==================== PAGE ACCUEIL (index.html) ====================
    @GetMapping("/")
    public String accueil(Model model) {
        List<DemandeCongeDTO> toutesDemandes = demandeCongeService.getAllDemandes();

        long totalDemandes = toutesDemandes.size();
        long acceptees = toutesDemandes.stream().filter(d -> d.getStatut().equals("ACCEPTE")).count();
        long refusees = toutesDemandes.stream().filter(d -> d.getStatut().equals("REFUSE")).count();
        double tauxAcceptation = totalDemandes > 0 ? (double) acceptees / totalDemandes * 100 : 0;

        List<DemandeCongeDTO> dernieresDemandes = toutesDemandes.stream()
                .limit(5)
                .collect(Collectors.toList());

        for (DemandeCongeDTO d : dernieresDemandes) {
            employeRepository.findById(d.getEmployeId()).ifPresent(emp ->
                    d.setEmployeDepartement(emp.getDepartement())
            );
        }

        model.addAttribute("totalDemandes", totalDemandes);
        model.addAttribute("demandesAcceptees", acceptees);
        model.addAttribute("demandesRefusees", refusees);
        model.addAttribute("tauxAcceptation", String.format("%.1f", tauxAcceptation) + "%");
        model.addAttribute("dernieresDemandes", dernieresDemandes);

        return "index";
    }

    // ==================== PAGE EMPLOYÉS ====================
    @GetMapping("/employes")
    public String employes(Model model) {
        model.addAttribute("employes", employeRepository.findAll());
        return "employes";
    }

    // ==================== PAGE TYPES DE CONGÉ ====================
    @GetMapping("/types-conge")
    public String typesConge(Model model) {
        model.addAttribute("typesConges", typeCongeRepository.findAll());
        return "types-conge";
    }

    // ==================== PAGE LISTE DES DEMANDES (demandes.html) ====================
    @GetMapping("/demandes")
    public String listDemandes(
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) String departement,
            @RequestParam(required = false) Long typeConge,
            Model model) {

        List<DemandeCongeDTO> demandes = demandeCongeService.getAllDemandes();

        if (statut != null && !statut.isEmpty()) {
            demandes = demandes.stream()
                    .filter(d -> d.getStatut().equals(statut))
                    .collect(Collectors.toList());
        }

        if (departement != null && !departement.isEmpty()) {
            demandes = demandes.stream()
                    .filter(d -> {
                        return employeRepository.findById(d.getEmployeId())
                                .map(emp -> emp.getDepartement().equals(departement))
                                .orElse(false);
                    })
                    .collect(Collectors.toList());
        }

        if (typeConge != null) {
            demandes = demandes.stream()
                    .filter(d -> d.getTypeCongeId().equals(typeConge))
                    .collect(Collectors.toList());
        }

        for (DemandeCongeDTO d : demandes) {
            employeRepository.findById(d.getEmployeId()).ifPresent(emp ->
                    d.setEmployeDepartement(emp.getDepartement())
            );
        }

        model.addAttribute("demandes", demandes);
        model.addAttribute("typesConges", typeCongeRepository.findAll());

        return "demandes";
    }

    // ==================== PAGE NOUVELLE DEMANDE (nouvelle-demande.html) ====================
    @GetMapping("/nouvelle-demande")
    public String nouvelleDemandeForm(Model model) {
        model.addAttribute("employes", employeRepository.findAll());
        model.addAttribute("typesConges", typeCongeRepository.findAll());
        return "nouvelle-demande";
    }

    // ==================== TRAITEMENT NOUVELLE DEMANDE ====================
    @PostMapping("/nouvelle-demande")
    public String soumettreDemande(
            @RequestParam Long employeId,
            @RequestParam Long typeCongeId,
            @RequestParam String dateDebut,
            @RequestParam String dateFin,
            @RequestParam(required = false) String motif,
            RedirectAttributes redirectAttributes) {

        try {
            LocalDate debut = LocalDate.parse(dateDebut);
            LocalDate fin = LocalDate.parse(dateFin);

            demandeCongeService.soumettreDemande(employeId, typeCongeId, debut, fin, motif);
            redirectAttributes.addFlashAttribute("message", " Demande soumise avec succès !");
            redirectAttributes.addFlashAttribute("success", true);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", " Erreur: " + e.getMessage());
            redirectAttributes.addFlashAttribute("success", false);
        }

        return "redirect:/nouvelle-demande";
    }

    // ==================== VALIDER UNE DEMANDE ====================
    @GetMapping("/valider/{id}")
    public String validerDemande(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            demandeCongeService.validerDemande(id);
            redirectAttributes.addFlashAttribute("message", " Demande validée !");
            redirectAttributes.addFlashAttribute("success", true);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", " Erreur: " + e.getMessage());
            redirectAttributes.addFlashAttribute("success", false);
        }
        return "redirect:/demandes";
    }

    // ==================== REJETER UNE DEMANDE ====================
    @GetMapping("/rejeter/{id}")
    public String rejeterDemande(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            demandeCongeService.rejeterDemande(id);
            redirectAttributes.addFlashAttribute("message", " Demande rejetée !");
            redirectAttributes.addFlashAttribute("success", true);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Erreur: " + e.getMessage());
            redirectAttributes.addFlashAttribute("success", false);
        }
        return "redirect:/demandes";
    }

    // ==================== PAGE STATISTIQUES (statistiques.html) ====================
    @GetMapping("/statistiques")
    public String statistiques(Model model) {
        Map<String, Long> joursParDepartement = statistiqueService.getJoursConsommesParDepartement();
        Map<String, Double> tauxParDepartement = statistiqueService.getTauxAcceptationParDepartement();

        long maxJours = joursParDepartement.values().stream().mapToLong(Long::longValue).max().orElse(1);

        List<DemandeCongeDTO> toutesDemandes = demandeCongeService.getAllDemandes();
        long totalDemandes = toutesDemandes.size();
        long acceptees = toutesDemandes.stream().filter(d -> d.getStatut().equals("ACCEPTE")).count();
        long refusees = toutesDemandes.stream().filter(d -> d.getStatut().equals("REFUSE")).count();
        long enAttente = toutesDemandes.stream().filter(d -> d.getStatut().equals("EN_ATTENTE")).count();
        double tauxGlobal = totalDemandes > 0 ? (double) acceptees / totalDemandes * 100 : 0;

        // Pour le camembert - valeurs par défaut si pas de données
        double pourcentageAccept = tauxGlobal;
        double pourcentageRefuse = 100 - pourcentageAccept;

        model.addAttribute("joursParDepartement", joursParDepartement);
        model.addAttribute("tauxParDepartement", tauxParDepartement);
        model.addAttribute("maxJours", maxJours);
        model.addAttribute("tauxGlobal", String.format("%.1f", tauxGlobal));
        model.addAttribute("totalDemandes", totalDemandes);
        model.addAttribute("demandesAcceptees", acceptees);
        model.addAttribute("demandesRefusees", refusees);
        model.addAttribute("demandesEnAttente", enAttente);
        model.addAttribute("pourcentageAccept", Math.round(pourcentageAccept));
        model.addAttribute("pourcentageRefuse", Math.round(pourcentageRefuse));

        return "statistiques";
     }// ==================== GESTION EMPLOYÉS ====================
    @PostMapping("/employes/ajouter")
    public String ajouterEmploye(@RequestParam String nom, @RequestParam String departement,
                                 @RequestParam String dateEmbauche, RedirectAttributes redirectAttributes) {
        try {
            Employe e = new Employe();
            e.setNom(nom);
            e.setDepartement(departement);
            e.setDateEmbauche(LocalDate.parse(dateEmbauche));
            employeRepository.save(e);
            redirectAttributes.addFlashAttribute("message", "✅ Employé ajouté !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "❌ Erreur: " + e.getMessage());
        }
        return "redirect:/employes";
    }

    @PostMapping("/employes/modifier")
    public String modifierEmploye(@RequestParam Long id, @RequestParam String nom,
                                  @RequestParam String departement, @RequestParam String dateEmbauche,
                                  RedirectAttributes redirectAttributes) {
        try {
            Employe e = employeRepository.findById(id).orElseThrow();
            e.setNom(nom);
            e.setDepartement(departement);
            e.setDateEmbauche(LocalDate.parse(dateEmbauche));
            employeRepository.save(e);
            redirectAttributes.addFlashAttribute("message", " Employé modifié !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", " Erreur: " + e.getMessage());
        }
        return "redirect:/employes";
    }

    @GetMapping("/employes/supprimer/{id}")
    public String supprimerEmploye(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            employeRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("message", " Employé supprimé !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", " Erreur: " + e.getMessage());
        }
        return "redirect:/employes";
  }// ==================== GESTION TYPES CONGÉ ====================

    @GetMapping("/types-conge/ajouter-form")
    public String ajouterTypeForm(Model model) {
        model.addAttribute("typeConge", new TypeConge());
        return "type-form";
    }

    @PostMapping("/types-conge/ajouter")
    public String ajouterType(@RequestParam String libelle, @RequestParam Integer quotaAnnuel,
                              RedirectAttributes redirectAttributes) {
        try {
            TypeConge t = new TypeConge();
            t.setLibelle(libelle);
            t.setQuotaAnnuel(quotaAnnuel);
            typeCongeRepository.save(t);
            redirectAttributes.addFlashAttribute("message", " Type ajouté !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", " Erreur: " + e.getMessage());
        }
        return "redirect:/types-conge";
    }

    @GetMapping("/types-conge/modifier/{id}")
    public String modifierTypeForm(@PathVariable Long id, Model model) {
        TypeConge t = typeCongeRepository.findById(id).orElseThrow();
        model.addAttribute("typeConge", t);
        return "type-form";
    }

    @PostMapping("/types-conge/modifier")
    public String modifierType(@RequestParam Long id, @RequestParam String libelle,
                               @RequestParam Integer quotaAnnuel, RedirectAttributes redirectAttributes) {
        try {
            TypeConge t = typeCongeRepository.findById(id).orElseThrow();
            t.setLibelle(libelle);
            t.setQuotaAnnuel(quotaAnnuel);
            typeCongeRepository.save(t);
            redirectAttributes.addFlashAttribute("message", " Type modifié !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", " Erreur: " + e.getMessage());
        }
        return "redirect:/types-conge";
    }

    @GetMapping("/types-conge/supprimer/{id}")
    public String supprimerType(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            typeCongeRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("message", " Type supprimé !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", " Erreur: " + e.getMessage());
        }
        return "redirect:/types-conge";



}}


