package org.example.infrastructure.apivalidation;


import org.example.infrastructure.apivalidation.dto.ApiValidationErrorResponseDto;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


import java.util.List;

import java.util.stream.Collectors;

@ControllerAdvice

public class ApiValidationErrorHandler {


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiValidationErrorResponseDto apiValidation(MethodArgumentNotValidException methodArgumentNotValidException) {
        List<String> errorsFromException = getErrorsFromException(methodArgumentNotValidException);
        return new ApiValidationErrorResponseDto(errorsFromException, HttpStatus.BAD_REQUEST);
    }

    private static List<String> getErrorsFromException(MethodArgumentNotValidException methodArgumentNotValidException) {
        return methodArgumentNotValidException
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
    }
}
