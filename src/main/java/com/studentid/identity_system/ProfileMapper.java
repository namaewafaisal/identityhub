package com.studentid.identity_system;

import org.mapstruct.*;
import org.springframework.stereotype.Component;

import com.studentid.identity_system.dto.HandleRequest;
import com.studentid.identity_system.dto.HandleResponse;
import com.studentid.identity_system.dto.ProfileRequest;
import com.studentid.identity_system.dto.ProfileResponse;
import com.studentid.identity_system.dto.UpdateProfileRequest;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

   @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "handle", ignore = true)  // ← never touch handle here
    void updateProfileFromDto(UpdateProfileRequest dto, @MappingTarget StudentProfile entity);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateHandleFromDto(HandleRequest dto,
                            @MappingTarget StudentHandle entity);

    ProfileResponse toResponse(StudentProfile profile);

    HandleResponse toHandleResponse(StudentHandle handle);
    StudentHandle toHandleEntity(HandleRequest request);
    StudentProfile toEntity(ProfileRequest request);

}