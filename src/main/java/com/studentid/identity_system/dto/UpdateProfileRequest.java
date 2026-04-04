package com.studentid.identity_system.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Schema(description = "Register number", example = "814723104089")
    private String registerNumber;

    @Schema(description = "Full name", example = "Mohamed Faisal")
    private String fullName;

    @Schema(description = "Date of birth (yyyy-MM-dd)", example = "2004-05-10")
    private LocalDate dateOfBirth;

    @Schema(description = "Gender", example = "Male")
    private String gender;

    @Schema(description = "Department", example = "CSE")
    private String department;

    @Schema(description = "Year (1-4)", example = "3")
    @Min(1)
    @Max(4)
    private Integer year;

    @Schema(description = "Section", example = "B")
    private String section;

    @Schema(description = "Batch", example = "Prime")
    private String batch;

    @Schema(description = "Phone number", example = "9876543210")
    @Size(min = 10, max = 15)
    private String phoneNumber;

    @Schema(description = "Personal email", example = "faisal@gmail.com")
    @Email
    private String personalEmail;

    @Schema(description = "Handle details (partial update supported)")
    private HandleRequest handle;
}