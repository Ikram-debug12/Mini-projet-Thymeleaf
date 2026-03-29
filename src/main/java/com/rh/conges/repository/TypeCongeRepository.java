package com.rh.conges.repository;

import com.rh.conges.entity.TypeConge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeCongeRepository extends JpaRepository<TypeConge, Long> {
}
