package com.pasteleria.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "AuthRequest", description = "Payload para login")
public class AuthRequest {
    @Schema(description = "Nombre de usuario", example = "usuario01")
    private String username;

    @Schema(description = "Contraseña", example = "secret123")
    private String password;
}
