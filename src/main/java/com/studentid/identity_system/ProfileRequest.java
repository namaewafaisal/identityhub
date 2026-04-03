package com.studentid.identity_system;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileRequest {

    @NotBlank
    private String registerNumber;

    @NotBlank
    private String fullName;

    private LocalDate dateOfBirth;

    private String gender;

    @NotBlank
    private String department;

    @Min(1)
    @Max(4)
    private Integer year;

    private String section;

    private String batch;

    @Size(min = 10, max = 15)
    private String phoneNumber;

    @Email
    private String personalEmail;

    private HandleRequest handle;
}