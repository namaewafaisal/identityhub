package com.studentid.identity_system;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {


    @Size(min = 1, message = "Full name cannot be empty")
    private String fullName;

    @Size(min = 1, message = "Department cannot be empty")
    private String department;

    @Min(1)
    @Max(4)
    private Integer year;
}