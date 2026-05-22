package com.clinic.patient_service.repository;

import com.clinic.patient_service.model.Patient;
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
}
