package org.example.domain.loginandregister;

import org.assertj.core.api.Assertions;
import org.example.domain.loginandregister.dto.RegisterUserDto;
import org.example.domain.loginandregister.dto.RegistrationResultDto;
import org.example.domain.loginandregister.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;


class LoginAndRegisterFacadeTest {

    LoginAndRegisterFacade loginFacade = new LoginAndRegisterFacade(
            new InMemoryLoginRepository()
    );

    @Test
    public void should_register_user() {
        //Given
        RegisterUserDto registerUserDto = new RegisterUserDto("Adam123", "password");
        //When
        RegistrationResultDto register = loginFacade.register(registerUserDto);


        //Then
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(register.created()).isTrue(),
                () -> Assertions.assertThat(register.username()).isEqualTo("Adam123")
        );

    }


    @Test
    public void should_find_user_by_username() {
        //Given
        RegisterUserDto registerUserDto = new RegisterUserDto("username", "password");
        RegistrationResultDto register = loginFacade.register(registerUserDto);

        //When
        UserDto byUsername = loginFacade.findByUsername(register.username());

        //Then
        Assertions.assertThat(byUsername).isEqualTo(new UserDto(byUsername.id(), "username", "password"));
    }


    @Test
    public void should_throw_exception_when_user_not_found() {
        //Given
        String username = "userTest";
        //When
        Throwable throwable = Assertions.catchThrowable(() -> loginFacade.findByUsername(username));
        //Then
        Assertions.assertThat(throwable)
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("User not found");

    }
}