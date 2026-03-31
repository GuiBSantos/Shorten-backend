package com.guibsantos.shorterURL.controller.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
        @NotBlank(message = "O email é obrigatório")
        @Email(message = "O email deve ser válido")
        String email
) {}
