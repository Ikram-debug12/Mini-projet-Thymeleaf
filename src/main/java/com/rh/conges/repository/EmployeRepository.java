
package com.rh.conges.repository;

import com.rh.conges.entity.Employe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmployeRepository extends JpaRepository<Employe, Long> {
    List<Employe> findByDepartement(String departement);
    boolean existsByNom(String nom);
}