package com.studentid.identity_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = """
Handles are stored as usernames only.
Full URLs are generated during response/export.
Example:
github = namaewafaisal → https://github.com/namaewafaisal
""")
@Data
public class HandleRequest {

    @Schema(description = "GitHub username (not full URL)", example = "namawafaisal")
    private String github;
    
    @Schema(description = "Leetcode username (not full URL)", example = "namaewafaisal")
    private String leetcode;
    
    @Schema(description = "Code forces username (not full URL)", example = "namaewafaisal")
    private String codeforces;
    
    @Schema(description = "Hackerrank username (not full URL)", example = "namaewafaisal")
    private String hackerrank;

    @Schema(description = "Linkedin username (not full URL)", example = "namaewafaisal")
    private String linkedin;
}