package com.studentid.identity_system.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Student profile response")
public class ProfileResponse {

    @Schema(example = "814723104089")
    private String registerNumber;

    @Schema(example = "Mohamed Faisal")
    private String fullName;

    @Schema(example = "2004-05-10")
    private LocalDate dateOfBirth;

    @Schema(example = "Male")
    private String gender;

    @Schema(example = "CSE")
    private String department;

    @Schema(example = "3")
    private Integer year;

    @Schema(example = "B")
    private String section;

    @Schema(example = "Prime")
    private String batch;

    @Schema(example = "9876543210")
    private String phoneNumber;

    @Schema(example = "faisal@gmail.com")
    private String personalEmail;

    @Schema(description = "User handles (usernames only)")
    private HandleResponse handle;
}