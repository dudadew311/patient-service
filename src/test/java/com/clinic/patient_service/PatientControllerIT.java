package com.clinic.patient_service;

import com.clinic.patient_service.model.Patient;
import com.clinic.patient_service.repository.PatientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
class PatientControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Patient savedPatient;

    @BeforeEach
    void setUp() {
        // Clear old database states to avoid unique constraint issues
        patientRepository.deleteAll();

        // Seed a fresh base record before each test execution
        Patient patient = Patient.builder()
                .firstName("John")
                .lastName("Doe")
                .email("johndoe@example.com")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .deleted(false)
                .build();

        savedPatient = patientRepository.save(patient);
    }

    @Test
    void createPatient_ShouldReturnCreated() throws Exception {
        Patient newPatient = Patient.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("janesmith@example.com")
                .dateOfBirth(LocalDate.of(1992, 8, 20))
                .build();

        mockMvc.perform(post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPatient)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("janesmith@example.com"));
    }

    @Test
    void getAllPatients_ShouldReturnPaginatedWrapper() throws Exception {
        mockMvc.perform(get("/api/v1/patients")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "lastName,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].lastName").value("Doe"))
                .andExpect(jsonPath("$.pageable.pageSize").value(5))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getAllPatients_WithFiltering_ShouldReturnMatchingContent() throws Exception {
        mockMvc.perform(get("/api/v1/patients")
                        .param("lastName", "doe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].lastName").value("Doe"));
    }

    @Test
    void updatePatient_ShouldReturnOkAndMessage() throws Exception {
        Patient updatedDetails = Patient.builder()
                .firstName("Johnathan")
                .lastName("Doe")
                .email("johndoe@example.com")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .build();

        mockMvc.perform(put("/api/v1/patients/{id}", savedPatient.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Success"))
                .andExpect(jsonPath("$.message").value("Patient record updated successfully"));
    }

    @Test
    void deletePatient_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/patients/{id}", savedPatient.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePatient_WhenNotFound_ShouldReturnNotFound() throws Exception {
        Long nonExistentPatientId = 999L;

        mockMvc.perform(delete("/api/v1/patients/{id}", nonExistentPatientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}