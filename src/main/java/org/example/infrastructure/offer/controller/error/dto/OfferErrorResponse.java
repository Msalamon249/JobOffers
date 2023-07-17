package org.example.infrastructure.offer.controller.error.dto;

import org.springframework.http.HttpStatus;

public record OfferErrorResponse(
        String message,
        HttpStatus status

) {
}
