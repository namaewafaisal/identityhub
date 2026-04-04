package com.studentid.identity_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "User handles (usernames only, not full URLs)")
public class HandleResponse {

    @Schema(example = "namaewafaisal")
    private String github;

    @Schema(example = "namaewafaisal")
    private String leetcode;

    @Schema(example = "namaewafaisal")
    private String codeforces;

    @Schema(example = "namaewafaisal")
    private String hackerrank;

    @Schema(example = "namaewafaisal")
    private String linkedin;
}