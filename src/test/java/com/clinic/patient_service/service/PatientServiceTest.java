package com.clinic.patient_service.service;

import com.clinic.patient_service.exception.ResourceNotFoundException;
import com.clinic.patient_service.model.Patient;
import com.clinic.patient_service.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

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
        samplePatient = Patient.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("johndoe@example.com")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .deleted(false)
                .build();
    }

    @Test
    void getAllPatients_ReturnsPaginatedPatients() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Patient> patientPage = new PageImpl<>(List.of(samplePatient));

        when(patientRepository.findAll(pageable)).thenReturn(patientPage);

        Page<Patient> result = patientService.getAllPatients(null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(patientRepository, times(1)).findAll(pageable);
    }

    @Test
    void getAllPatients_WithLastNameFilter_ReturnsFilteredPatients() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Patient> patientPage = new PageImpl<>(List.of(samplePatient));

        when(patientRepository.findByLastNameContainingIgnoreCase("Doe", pageable)).thenReturn(patientPage);

        Page<Patient> result = patientService.getAllPatients("Doe", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(patientRepository, times(1)).findByLastNameContainingIgnoreCase("Doe", pageable);
    }

    @Test
    void createPatient_Success() {
        when(patientRepository.existsByEmail(samplePatient.getEmail())).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenReturn(samplePatient);

        Patient savedPatient = patientService.createPatient(samplePatient);

        assertNotNull(savedPatient);
        assertEquals("johndoe@example.com", savedPatient.getEmail());
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void createPatient_ThrowsException_WhenEmailExists() {
        when(patientRepository.existsByEmail(samplePatient.getEmail())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            patientService.createPatient(samplePatient);
        });

        assertEquals("A patient with this email already exists.", exception.getMessage());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void deletePatient_WhenPatientExists_ShouldDeleteSuccessfully() {
        Long patientId = 1L;
        when(patientRepository.existsById(patientId)).thenReturn(true);
        doNothing().when(patientRepository).deleteById(patientId);

        patientService.deletePatient(patientId);

        verify(patientRepository, times(1)).existsById(patientId);
        verify(patientRepository, times(1)).deleteById(patientId);
    }

    @Test
    void deletePatient_WhenPatientDoesNotExist_ShouldThrowException() {
        Long patientId = 1L;
        when(patientRepository.existsById(patientId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            patientService.deletePatient(patientId);
        });

        verify(patientRepository, times(1)).existsById(patientId);
        verify(patientRepository, never()).deleteById(anyLong());
    }
}