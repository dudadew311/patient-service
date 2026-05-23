package com.clinic.patient_service.controller;

import com.clinic.patient_service.model.Patient;
import com.clinic.patient_service.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {

    private final PatientService patientService;

    // Constructor injection for seamless testing
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @PostMapping
    @Operation(summary = "Register a new patient", description = "Creates a new patient record. Validation ensures the email is unique and fields are populated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Patient successfully registered"),
            @ApiResponse(responseCode = "400", description = "Validation failed or invalid payload structural format")
    })

    public ResponseEntity<Patient> createPatient(@Valid @RequestBody Patient patient) {
        Patient savedPatient = patientService.createPatient(patient);
        return new ResponseEntity<>(savedPatient, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody Patient patientDetails) {

        patientService.updatePatient(id, patientDetails);

        // Return a clean, safe JSON structure instead of the managed Hibernate proxy object
        return ResponseEntity.ok().body(java.util.Map.of(
                "status", "Success",
                "message", "Patient record updated successfully"
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
