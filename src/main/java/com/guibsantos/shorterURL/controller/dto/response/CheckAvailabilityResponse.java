package com.guibsantos.shorterURL.controller.dto.response;

public record CheckAvailabilityResponse(
        boolean isAvailable,
        String message
) {
}
