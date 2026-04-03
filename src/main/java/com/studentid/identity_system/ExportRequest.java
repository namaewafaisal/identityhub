package com.studentid.identity_system;

import java.util.List;
import lombok.Data;

@Data
public class ExportRequest {

    private List<String> fields;

    // filters
    private String department;
    private Integer year;
    private String section;
    private String name;
}