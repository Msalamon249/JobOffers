package org.example.infrastructure.loginandregister.controller.error;

import lombok.extern.log4j.Log4j2;
import org.example.domain.loginandregister.UsernameDuplicateException;
import org.example.infrastructure.loginandregister.controller.error.dto.RegisterErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Log4j2
public class RegisterControllerErrorHandler {


    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    @ExceptionHandler(UsernameDuplicateException.class)
    public RegisterErrorResponseDto usernameDuplicateErrorHandler(UsernameDuplicateException exception){
        String message = exception.getMessage();
        log.error(exception.getMessage());
        return new RegisterErrorResponseDto(message,HttpStatus.CONFLICT);
    }
}
