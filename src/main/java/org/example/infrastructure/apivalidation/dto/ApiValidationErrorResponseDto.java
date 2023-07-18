package org.example.infrastructure.apivalidation.dto;

import org.springframework.http.HttpStatus;

import java.util.List;


public record
ApiValidationErrorResponseDto(
        List<String> messages,
        HttpStatus status

) {
}
