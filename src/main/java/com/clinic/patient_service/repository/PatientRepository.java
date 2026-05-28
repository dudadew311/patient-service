package com.clinic.patient_service.repository;

import com.clinic.patient_service.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<Patient> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);
}
