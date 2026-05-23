package com.clinic.patient_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
class PatientControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deletePatient_ShouldReturnNoContent() throws Exception {
        Long sampleId = 1L;

        mockMvc.perform(delete("/api/v1/patients/{id}", sampleId)
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