package com.rh.conges.controller;

import com.rh.conges.service.StatistiqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/statistiques")
@CrossOrigin(origins = "*")
public class StatistiqueController {

    @Autowired
    private StatistiqueService statistiqueService;

    @GetMapping("/jours-par-departement")
    public Map<String, Long> getJoursConsommesParDepartement() {
        return statistiqueService.getJoursConsommesParDepartement();
    }

    @GetMapping("/taux-acceptation")
    public Map<String, Object> getTauxAcceptation() {
        Map<String, Object> result = new HashMap<>();
        result.put("tauxGlobal", statistiqueService.getTauxAcceptationGlobal());
        result.put("tauxParDepartement", statistiqueService.getTauxAcceptationParDepartement());
        return result;
    }

    @GetMapping("/completes")
    public Map<String, Object> getStatistiquesCompletes() {
        return statistiqueService.getStatistiquesCompletes();
    }
}
