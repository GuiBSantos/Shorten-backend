package com.guibsantos.shorterURL.controller.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "O email é obrigatório")
        @Email(message = "O email deve ser válido")
        String email,

        @NotBlank(message = "O código é obrigatório")
        String code,

        @NotBlank(message = "A nova senha é obrigatória")
        @Size(min = 6, message = "A nova senha deve ter no mínimo 6 caracteres")
        String newPassword
) {}
