package com.studentid.identity_system;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Size(min = 1)
    private String registerNumber;

    @Size(min = 1)
    private String fullName;

    private LocalDate dateOfBirth;

    private String gender;

    @Size(min = 1)
    private String department;

    @Min(1)
    @Max(4)
    private Integer year;

    private String section;

    private String batch;

    private String phoneNumber;

    private String personalEmail;

    private HandleRequest handle;
}