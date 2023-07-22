package org.example.infrastructure.loginandregister.controller;

import lombok.AllArgsConstructor;
import org.example.domain.loginandregister.LoginAndRegisterFacade;
import org.example.domain.loginandregister.dto.RegisterUserDto;
import org.example.domain.loginandregister.dto.RegistrationResultDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import javax.validation.Valid;
import java.net.URI;

@RestController
@AllArgsConstructor
public class RegisterController {

    private final LoginAndRegisterFacade loginAndRegisterFacade;
    private final PasswordEncoder passwordEncoder;


    @PostMapping("/register")
    public ResponseEntity<RegistrationResultDto> registerUser(@RequestBody @Valid RegisterUserDto registerUserDto) {
        String encode = passwordEncoder.encode(registerUserDto.password());

        RegistrationResultDto registered = loginAndRegisterFacade.register(
                new RegisterUserDto(registerUserDto.username(), encode));

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(registered.id())
                .toUri();

        return ResponseEntity.created(uri).body(registered);
    }
}
