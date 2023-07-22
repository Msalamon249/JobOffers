package org.example.domain.loginandregister.dto;



import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record RegisterUserDto(
        @NotBlank(message = "{username.not.blank}")
        String username,
        @NotBlank(message = "{password.not.blank}")
        @Size(min = 2, max = 20)
        String password) {
}


