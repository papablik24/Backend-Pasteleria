package com.pasteleria.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "AuthRequest", description = "Payload para login")
public class AuthRequest {
    @Schema(description = "Nombre de usuario o correo electrónico", example = "admin@pasteleria.com")
    @JsonAlias({"nombreUsuario", "correoElectronico", "email", "user"})
    private String username;

    @Schema(description = "Contraseña", example = "admin123")
    @JsonAlias({"contrasena", "pass"})
    private String password;
}
