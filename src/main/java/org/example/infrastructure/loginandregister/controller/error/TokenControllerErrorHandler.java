package org.example.infrastructure.loginandregister.controller.error;

import lombok.extern.log4j.Log4j2;
import org.example.infrastructure.loginandregister.controller.error.dto.LoginErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Log4j2
public class TokenControllerErrorHandler {

    private static final String BAD_CREDENTIALS = "Bad Credentials";

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    @ExceptionHandler(BadCredentialsException.class)
    public LoginErrorResponseDto userNotFoundErrorHandler() {
        return new LoginErrorResponseDto(BAD_CREDENTIALS, HttpStatus.UNAUTHORIZED);
    }

}
