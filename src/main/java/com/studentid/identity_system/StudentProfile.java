package com.studentid.identity_system;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String department;
    private Integer year;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}