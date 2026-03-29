package com.rh.conges.controller;

import com.rh.conges.entity.Employe;
import com.rh.conges.repository.EmployeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/employes")
@CrossOrigin(origins = "*")
public class EmployeController {

    @Autowired
    private EmployeRepository employeRepository;

    @GetMapping
    public List<Employe> getAllEmployes() {
        return employeRepository.findAll();
    }

    @PostMapping
    public Employe createEmploye(@RequestBody Employe employe) {
        return employeRepository.save(employe);
    }
}