package com.yourpackage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PatientControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deletePatient_ShouldReturnNoContent() throws Exception {
        // Assumes ID 1 exists in your test seed data/migrations
        Long existingPatientId = 1L;

        mockMvc.perform(delete("/api/v1/patients/{id}", existingPatientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); // Validates 204 status
    }

    @Test
    void deletePatient_WhenNotFound_ShouldReturnNotFound() throws Exception {
        Long nonExistentPatientId = 999L;

        mockMvc.perform(delete("/api/v1/patients/{id}", nonExistentPatientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Validates 404 status
    }
}
