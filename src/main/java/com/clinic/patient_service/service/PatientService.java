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

    @Transactional
    public Patient updatePatient(Long id, Patient patientDetails) {
        // 1. Fetch the existing record or throw our clean 404 custom exception
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));

        // 2. Enforce email uniqueness check if they are trying to change their email string
        if (!existingPatient.getEmail().equals(patientDetails.getEmail()) &&
                patientRepository.existsByEmail(patientDetails.getEmail())) {
            throw new IllegalArgumentException("A patient with this email already exists.");
        }

        // 3. Map the updated fields over to our tracked Hibernate entity
        existingPatient.setFirstName(patientDetails.getFirstName());
        existingPatient.setLastName(patientDetails.getLastName());
        existingPatient.setEmail(patientDetails.getEmail());
        existingPatient.setDateOfBirth(patientDetails.getDateOfBirth());

        // 4. Save and return the updated entity (triggers @PreUpdate timestamp automatically)
        return patientRepository.save(existingPatient);
    }

    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patient not found with id: " + id);
        }
        patientRepository.deleteById(id);
    }
}