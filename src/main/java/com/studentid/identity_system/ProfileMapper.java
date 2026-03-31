package com.studentid.identity_system;

import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProfileFromDto(UpdateProfileRequest dto,
                              @MappingTarget StudentProfile entity);

    ProfileResponse toResponse(StudentProfile profile);
}