package org.example.infrastructure.loginandregister.controller;


import lombok.AllArgsConstructor;
import org.example.infrastructure.loginandregister.controller.dto.JwtResponseDto;
import org.example.infrastructure.loginandregister.controller.dto.LoginRequestDto;
import org.example.infrastructure.seciurity.jwt.JwtAuthenticatorFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
public class TokenController {

    private final JwtAuthenticatorFacade jwtAuthenticator;


    @PostMapping("/token")
    public ResponseEntity<JwtResponseDto> authenticateAndGenerateToken(@RequestBody @Valid LoginRequestDto loginRequestDto) {
        final JwtResponseDto jwtResponseDto = jwtAuthenticator.authenticateAndGenerateToken(loginRequestDto);
        return ResponseEntity.ok(jwtResponseDto);
    }
}
