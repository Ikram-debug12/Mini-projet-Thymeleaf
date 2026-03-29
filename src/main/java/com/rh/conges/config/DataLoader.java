package com.rh.conges.config;

import com.rh.conges.entity.Employe;
import com.rh.conges.entity.TypeConge;
import com.rh.conges.repository.EmployeRepository;
import com.rh.conges.repository.TypeCongeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private TypeCongeRepository typeCongeRepository;

    @Override
    public void run(String... args) throws Exception {

        // Ajouter les types de congé
        if (typeCongeRepository.count() == 0) {
            TypeConge cp = new TypeConge();
            cp.setLibelle("Congé Payé");
            cp.setQuotaAnnuel(30);
            typeCongeRepository.save(cp);

            TypeConge maladie = new TypeConge();
            maladie.setLibelle("Congé Maladie");
            maladie.setQuotaAnnuel(10);
            typeCongeRepository.save(maladie);

            TypeConge sansSolde = new TypeConge();
            sansSolde.setLibelle("Congé Sans Solde");
            sansSolde.setQuotaAnnuel(15);
            typeCongeRepository.save(sansSolde);

            System.out.println("✅ Types de congé ajoutés");
        }

        // Ajouter les employés
        if (employeRepository.count() == 0) {
            Employe emp1 = new Employe();
            emp1.setNom("Dupont Jean");
            emp1.setDepartement("Informatique");
            emp1.setDateEmbauche(LocalDate.of(2020, 1, 15));
            employeRepository.save(emp1);

            Employe emp2 = new Employe();
            emp2.setNom("Martin Sophie");
            emp2.setDepartement("Ressources Humaines");
            emp2.setDateEmbauche(LocalDate.of(2021, 3, 10));
            employeRepository.save(emp2);

            Employe emp3 = new Employe();
            emp3.setNom("Bernard Pierre");
            emp3.setDepartement("Comptabilité");
            emp3.setDateEmbauche(LocalDate.of(2019, 6, 20));
            employeRepository.save(emp3);

            Employe emp4 = new Employe();
            emp4.setNom("Petit Marie");
            emp4.setDepartement("Informatique");
            emp4.setDateEmbauche(LocalDate.of(2022, 2, 1));
            employeRepository.save(emp4);

            System.out.println("✅ Employés ajoutés");
        }
    }
}