package com.clinic.patient_service.repository;

import com.clinic.patient_service.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // Custom query method to find a patient by email
    // Spring Data JPA parses this name automatically to generate the underlying SQL
    Optional<Patient> findByEmail(String email);

    // Custom query method to check if an email already exists in the system
    boolean existsByEmail(String email);

    // Finds active patients by last name with built-in pagination and sorting
    // (Hibernate automatically filters out deleted records due to the @Where clause)
    Page<Patient> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);
}
