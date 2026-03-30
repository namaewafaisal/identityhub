package com.studentid.identity_system;

import lombok.Data;

@Data
public class ProfileRequest {
    private String fullName;
    private String department;
    private int year;
}