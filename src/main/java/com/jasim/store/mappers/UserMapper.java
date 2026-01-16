package com.jasim.store.mappers;

import com.jasim.store.dtos.CreateUserRequest;
import com.jasim.store.dtos.UpdateUserRequest;
import com.jasim.store.dtos.UserDto;
import com.jasim.store.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    User toEntity(CreateUserRequest request);
    void toUpdateUser(UpdateUserRequest request, @MappingTarget User user);
}
