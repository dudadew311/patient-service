package com.clinic.patient_service.service;

import com.clinic.patient_service.model.Patient;
import com.clinic.patient_service.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private Patient samplePatient;

    @BeforeEach
    void setUp() {
        // Initialize a clean patient object before each test run
        samplePatient = Patient.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("johndoe@example.com")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .build();
    }

    @Test
    void createPatient_Success() {
        // Arrange: Mock repository to say email does NOT exist, and return the saved patient
        when(patientRepository.existsByEmail(samplePatient.getEmail())).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenReturn(samplePatient);

        // Act: Execute the service method
        Patient savedPatient = patientService.createPatient(samplePatient);

        // Assert: Verify the data matches expectations
        assertNotNull(savedPatient);
        assertEquals("johndoe@example.com", savedPatient.getEmail());

        // Verify the repository save method was actually triggered exactly once
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void createPatient_ThrowsException_WhenEmailExists() {
        // Arrange: Mock repository to simulate that the email is ALREADY taken
        when(patientRepository.existsByEmail(samplePatient.getEmail())).thenReturn(true);

        // Act & Assert: Verify that the service throws an IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            patientService.createPatient(samplePatient);
        });

        assertEquals("A patient with this email already exists.", exception.getMessage());

        // Verify that save was NEVER called because the validation blocked it
        verify(patientRepository, never()).save(any(Patient.class));
    }
}