package com.rh.conges.controller;

import com.rh.conges.dto.DemandeCongeDTO;
import com.rh.conges.entity.DemandeConge;
import com.rh.conges.enums.StatutDemande;
import com.rh.conges.service.DemandeCongeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/demandes")
@CrossOrigin(origins = "*")
public class DemandeCongeController {

    @Autowired
    private DemandeCongeService demandeCongeService;

    // Soumettre une demande
    @PostMapping("/soumettre")
    public ResponseEntity<?> soumettreDemande(
            @RequestParam Long employeId,
            @RequestParam Long typeCongeId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateDebut,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFin,
            @RequestParam(required = false) String motif) {
        try {
            DemandeConge demande = demandeCongeService.soumettreDemande(
                    employeId, typeCongeId, dateDebut, dateFin, motif);
            return ResponseEntity.status(HttpStatus.CREATED).body(demande);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Valider une demande
    @PutMapping("/{id}/valider")
    public ResponseEntity<?> validerDemande(@PathVariable Long id) {
        try {
            DemandeConge demande = demandeCongeService.validerDemande(id);
            return ResponseEntity.ok(demande);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Rejeter une demande
    @PutMapping("/{id}/rejeter")
    public ResponseEntity<?> rejeterDemande(@PathVariable Long id) {
        try {
            DemandeConge demande = demandeCongeService.rejeterDemande(id);
            return ResponseEntity.ok(demande);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Quota restant
    @GetMapping("/quota")
    public ResponseEntity<?> getQuotaRestant(
            @RequestParam Long employeId,
            @RequestParam Long typeCongeId) {
        try {
            long quotaRestant = demandeCongeService.getQuotaRestant(employeId, typeCongeId);
            Map<String, Object> response = new HashMap<>();
            response.put("employeId", employeId);
            response.put("typeCongeId", typeCongeId);
            response.put("quotaRestant", quotaRestant);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Filtrer par statut
    @GetMapping("/statut/{statut}")
    public List<DemandeCongeDTO> filtrerParStatut(@PathVariable StatutDemande statut) {
        return demandeCongeService.filtrerParStatut(statut);
    }

    // Filtrer par département
    @GetMapping("/departement/{departement}")
    public List<DemandeCongeDTO> filtrerParDepartement(@PathVariable String departement) {
        return demandeCongeService.filtrerParDepartement(departement);
    }

    // Filtrer par période
    @GetMapping("/periode")
    public List<DemandeCongeDTO> filtrerParPeriode(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate debut,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fin) {
        return demandeCongeService.filtrerParPeriode(debut, fin);
    }

    // Filtrer par type de congé
    @GetMapping("/type-conge/{typeCongeId}")
    public List<DemandeCongeDTO> filtrerParTypeConge(@PathVariable Long typeCongeId) {
        return demandeCongeService.filtrerParTypeConge(typeCongeId);
    }

    // Toutes les demandes
    @GetMapping
    public List<DemandeCongeDTO> getAllDemandes() {
        return demandeCongeService.getAllDemandes();
    }
}
