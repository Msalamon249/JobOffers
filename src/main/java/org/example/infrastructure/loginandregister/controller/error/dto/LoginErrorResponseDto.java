package org.example.infrastructure.loginandregister.controller.error.dto;

import org.springframework.http.HttpStatus;

public record LoginErrorResponseDto(String message, HttpStatus status) {
}
