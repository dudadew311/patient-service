package com.clinic.patient_service.service;

import com.clinic.patient_service.exception.ResourceNotFoundException;
import com.clinic.patient_service.model.Patient;
import com.clinic.patient_service.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    // Constructor injection (Preferred over @Autowired for testability)
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Transactional(readOnly = true)
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
    }

    @Transactional
    public Patient createPatient(Patient patient) {
        // Enforce business rule: Emails must be unique
        if (patientRepository.existsByEmail(patient.getEmail())) {
            throw new IllegalArgumentException("A patient with this email already exists.");
        }
        return patientRepository.save(patient);
    }
}