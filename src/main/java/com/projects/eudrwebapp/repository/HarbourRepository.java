package com.projects.eudrwebapp.repository;

import com.projects.eudrwebapp.model.Harbour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.OptionalDouble;

@Repository
public interface HarbourRepository extends JpaRepository<Harbour, Long> {

    Optional<Harbour> findByName(String harbourName);
}
