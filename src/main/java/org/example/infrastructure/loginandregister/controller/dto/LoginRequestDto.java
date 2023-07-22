package org.example.infrastructure.loginandregister.controller.dto;

import javax.validation.constraints.NotBlank;

public record LoginRequestDto(
        @NotBlank(message = "{username.not.blank}")
        String username,
        @NotBlank(message = "{password.not.blank}")
        String password
) {

}
