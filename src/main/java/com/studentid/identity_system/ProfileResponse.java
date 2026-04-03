package com.studentid.identity_system;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {

    private String registerNumber;
    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String department;
    private Integer year;
    private String section;
    private String batch;
    private String phoneNumber;
    private String personalEmail;

    private HandleResponse handle;
}