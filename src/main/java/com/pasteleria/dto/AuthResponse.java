package com.pasteleria.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(name = "AuthResponse", description = "Respuesta de login con token JWT")
public class AuthResponse {
    @Schema(description = "Token JWT", example = "eyJhbGci...")
    private String token;
}
