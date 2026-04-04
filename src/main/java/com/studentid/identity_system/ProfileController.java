package com.studentid.identity_system;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.studentid.identity_system.dto.ExportRequest;
import com.studentid.identity_system.dto.ProfileRequest;
import com.studentid.identity_system.dto.ProfileResponse;
import com.studentid.identity_system.dto.UpdateProfileRequest;
import com.studentid.identity_system.dto.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Profile", description = "Student profile operations")
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    // 🔹 CREATE PROFILE
    @Operation(
        summary = "Create profile",
        description = "Creates profile for authenticated user (only once)"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Profile created successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Profile already exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping
    public void createProfile(
            @Valid @RequestBody ProfileRequest request,
            @Parameter(hidden = true) Authentication authentication
    ) {
        String userId = (String) authentication.getPrincipal();
        profileService.createProfile(request, userId);
    }

    // 🔹 GET PROFILE
    @Operation(
        summary = "Get current user's profile",
        description = "Returns profile of the authenticated user"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Profile fetched successfully",
            content = @Content(schema = @Schema(implementation = ProfileResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Profile not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping
    public ProfileResponse getProfile(
            @Parameter(hidden = true) Authentication authentication
    ) {
        String userId = (String) authentication.getPrincipal();
        return profileService.getProfile(userId);
    }

    // 🔹 UPDATE PROFILE
    @Operation(
        summary = "Update profile",
        description = "Updates profile fields. Only non-null fields are updated."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Profile updated successfully",
            content = @Content(schema = @Schema(implementation = ProfileResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Profile not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PatchMapping
    public ProfileResponse updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @Parameter(hidden = true) Authentication authentication
    ) {
        String userId = (String) authentication.getPrincipal();
        return profileService.updateProfile(userId, request);
    }

    // 🔹 SEARCH
    @Operation(summary = "Search profile by register number")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Profile found",
            content = @Content(schema = @Schema(implementation = ProfileResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Student not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/search")
    public ProfileResponse searchByRegisterNumber(
            @Parameter(description = "Register number", example = "814723104089")
            @RequestParam String registerNumber
    ) {
        return profileService.getProfileByRegisterNumber(registerNumber);
    }

    // 🔹 FILTER
    @Operation(
        summary = "Filter student profiles",
        description = "Filters profiles based on department, year, and name with pagination"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Profiles fetched",
            content = @Content(schema = @Schema(implementation = ProfileResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid parameters",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/filter")
    public Page<ProfileResponse> filter(

            @Parameter(description = "Department filter", example = "CSE")
            @RequestParam(required = false) String department,

            @Parameter(description = "Year filter (1-4)", example = "3")
            @RequestParam(required = false) Integer year,

            @Parameter(description = "Search by name", example = "Faisal")
            @RequestParam(required = false) String name,

            @Parameter(description = "Pagination info (page, size, sort). Default size = 30")
            @PageableDefault(page = 0, size = 30) Pageable pageable
    ) {
        return profileService.filter(department, year, name, pageable);
    }

    // 🔹 EXPORT
    @Operation(
        summary = "Export profiles as Excel",
        description = "Exports filtered student data into Excel file. Department and year are mandatory."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Excel file generated successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No data found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("/export")
    public ResponseEntity<byte[]> export(
            @Valid @RequestBody ExportRequest request
    ) {

        byte[] excel = profileService.export(request);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        headers.add("Content-Disposition", "attachment; filename=profiles.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(excel);
    }
}