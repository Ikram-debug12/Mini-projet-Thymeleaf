package com.rh.conges.controller;

import com.rh.conges.entity.TypeConge;
import com.rh.conges.repository.TypeCongeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/types-conge")
@CrossOrigin(origins = "*")
public class TypeCongeController {

    @Autowired
    private TypeCongeRepository typeCongeRepository;

    @GetMapping
    public List<TypeConge> getAllTypes() {
        return typeCongeRepository.findAll();
    }

    @PostMapping
    public TypeConge createType(@RequestBody TypeConge typeConge) {
        return typeCongeRepository.save(typeConge);
    }
}
