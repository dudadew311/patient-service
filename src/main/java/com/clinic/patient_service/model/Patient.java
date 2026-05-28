package com.clinic.patient_service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE patients SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Schema(description = "Patient entity representing medical registration and identity records")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique database identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "First name is required")
    @Column(name = "first_name", nullable = false)
    @Schema(description = "Patient's legal first name", example = "John")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Column(name = "last_name", nullable = false)
    @Schema(description = "Patient's legal last name", example = "Doe")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(name = "email", nullable = false, unique = true)
    @Schema(description = "Unique contact email address used for notifications", example = "johndoe@example.com")
    private String email;

    @NotNull(message = "Date of birth is required")
    @Column(name = "date_of_birth", nullable = false)
    @Schema(description = "Patient's date of birth", example = "1990-05-15")
    private LocalDate dateOfBirth;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    @Schema(description = "Soft-delete flag indicating active or archived status", example = "false", accessMode = Schema.AccessMode.READ_ONLY)
    private boolean deleted = Boolean.FALSE;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "Timestamp when the record was initially registered", example = "2026-05-23T14:58:27", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    @Schema(description = "Timestamp when the record was last modified", example = "2026-05-23T15:02:11", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}