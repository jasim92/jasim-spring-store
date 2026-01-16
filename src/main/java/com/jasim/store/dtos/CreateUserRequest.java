package com.jasim.store.dtos;

import com.jasim.store.validations.Lowercase;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotBlank(message = "username is required")
    @Size(max = 255, message = "Must be less than 255 characters")
    private String name;

    @NotBlank(message = "email is required")
    @Email(message = "enter correct format")
    @Lowercase(message = "email must be in lowercase")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 12, message = "password must between 6 and 12 characters long.")
    private String password;
}
