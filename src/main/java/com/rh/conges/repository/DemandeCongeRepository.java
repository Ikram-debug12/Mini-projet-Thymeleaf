package com.rh.conges.repository;

import com.rh.conges.entity.DemandeConge;
import com.rh.conges.enums.StatutDemande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DemandeCongeRepository extends JpaRepository<DemandeConge, Long> {

    // Filtrer par statut
    List<DemandeConge> findByStatut(StatutDemande statut);

    // Filtrer par employé
    List<DemandeConge> findByEmployeId(Long employeId);

    // Filtrer par type de congé
    List<DemandeConge> findByTypeCongeId(Long typeCongeId);

    // Filtrer par département
    @Query("SELECT d FROM DemandeConge d WHERE d.employe.departement = :departement")
    List<DemandeConge> findByDepartement(@Param("departement") String departement);

    // Filtrer par période
    @Query("SELECT d FROM DemandeConge d WHERE d.dateDebut >= :debut AND d.dateFin <= :fin")
    List<DemandeConge> findByPeriode(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

    // Demandes acceptées d'un employé pour un type de congé
    @Query("SELECT d FROM DemandeConge d WHERE d.employe.id = :employeId AND d.typeConge.id = :typeCongeId AND d.statut = 'ACCEPTE'")
    List<DemandeConge> findDemandesAcceptees(@Param("employeId") Long employeId, @Param("typeCongeId") Long typeCongeId);
}