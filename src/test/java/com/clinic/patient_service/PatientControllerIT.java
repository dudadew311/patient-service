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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
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
        patientRepository.deleteAll();

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
    void getAllPatients_ShouldReturnPaginatedWrapper_WithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/patients")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void createPatient_AsAdmin_ShouldReturnCreated() throws Exception {
        Patient newPatient = Patient.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("janesmith@example.com")
                .dateOfBirth(LocalDate.of(1992, 8, 20))
                .build();

        mockMvc.perform(post("/api/v1/patients")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPatient)))
                .andExpect(status().isCreated());
    }

    @Test
    void createPatient_AsStaff_ShouldReturnForbidden() throws Exception {
        Patient newPatient = Patient.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("janesmith@example.com")
                .dateOfBirth(LocalDate.of(1992, 8, 20))
                .build();

        mockMvc.perform(post("/api/v1/patients")
                        .with(httpBasic("staff", "staff123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPatient)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deletePatient_AsAdmin_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/patients/{id}", savedPatient.getId())
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePatient_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/v1/patients/{id}", savedPatient.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}