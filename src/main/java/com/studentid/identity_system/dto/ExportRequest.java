package com.studentid.identity_system.dto;

import java.util.List;

import com.studentid.identity_system.ProfileField;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = """
Fields to export.

Rules:
- null → exports all fields
- empty list → error
- order is preserved in Excel
""")
@Data
public class ExportRequest {

    @Schema(description = "Fields to export. If null → all fields", example = "[\"FULL_NAME\", \"DEPARTMENT\"]")
    private List<ProfileField> fields;

    @Schema(description = "Department filter (mandatory)", example = "CSE")
    @NotBlank
    private String department;

    @Schema(description = "Year (1 to 4)", example = "3")
    @Min(1)
    @Max(4)
    @NotNull
    private Integer year;

    @Schema(description = "Section filter (optional)", example = "B")
    private String section;

    @Schema(description = "Search by name (optional)", example = "Faisal")
    private String name;
}