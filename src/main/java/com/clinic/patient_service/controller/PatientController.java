package com.clinic.patient_service.controller;

import com.clinic.patient_service.model.Patient;
import com.clinic.patient_service.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patients")
@Tag(name = "Patient Management", description = "Endpoints for handling patient lifecycles")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    @Operation(summary = "Get a paginated list of patients", description = "Retrieves patients with optional filtering by last name, sorting, and pagination options.")
    public ResponseEntity<Page<Patient>> getAllPatients(
            @RequestParam(required = false) String lastName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {

        // Default fallback sorting values
        String sortField = "id";
        Sort.Direction sortDirection = Sort.Direction.ASC;

        // Safely parse sorting configurations whether they come in as an array or comma-separated string
        if (sort != null && sort.length > 0) {
            if (sort[0].contains(",")) {
                // Handles ?sort=lastName,desc
                String[] parts = sort[0].split(",");
                sortField = parts[0];
                if (parts.length > 1 && parts[1].equalsIgnoreCase("desc")) {
                    sortDirection = Sort.Direction.DESC;
                }
            } else {
                // Handles ?sort=lastName&sort=desc
                sortField = sort[0];
                if (sort.length > 1 && sort[1].equalsIgnoreCase("desc")) {
                    sortDirection = Sort.Direction.DESC;
                }
            }
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));
        Page<Patient> patientPage = patientService.getAllPatients(lastName, pageable);

        return ResponseEntity.ok(patientPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get patient by ID", description = "Retrieves a single active patient record by its unique database identifier.")
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
    @Operation(summary = "Update patient details", description = "Modifies an existing patient's details based on their unique identifier.")
    public ResponseEntity<?> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody Patient patientDetails) {

        patientService.updatePatient(id, patientDetails);

        return ResponseEntity.ok().body(java.util.Map.of(
                "status", "Success",
                "message", "Patient record updated successfully"
        ));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a patient", description = "Performs a soft-delete on the patient record using their unique database identifier.")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}