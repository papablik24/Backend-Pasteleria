package com.pasteleria.controller;

import com.pasteleria.model.Usuarios;
import com.pasteleria.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.security.authentication.AuthenticationManager;
import com.pasteleria.util.JwtUtil;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Operaciones de autenticación y registro")
public class AuthController {

    @Autowired private final UsuarioService userService;
    @Autowired private final AuthenticationManager authenticationManager;
    @Autowired private final JwtUtil jwtUtil;

    public AuthController(UsuarioService userService, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/registro")
    @Operation(summary = "Registrar usuario", description = "Registra un nuevo usuario en la aplicación")
    @ApiResponse(responseCode = "200", description = "Usuario registrado", content = @Content(schema = @Schema(implementation = Usuarios.class)))
    public ResponseEntity<?> register(@RequestBody Usuarios user) {
        if (user.getNombreUsuario() == null || user.getContrasena() == null) {
            return ResponseEntity.badRequest().body("username y password requeridos");
        }
        if (userService.findByNombreUsuario(user.getNombreUsuario()).isPresent()) {
            return ResponseEntity.badRequest().body("username ya existe");
        }
        Usuarios saved = userService.register(user);
        saved.setContrasena(null);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Autentica al usuario y devuelve un JWT")
    @ApiResponse(responseCode = "200", description = "Token JWT", content = @Content(schema = @Schema(implementation = com.pasteleria.dto.AuthResponse.class)))
    public ResponseEntity<?> login(@RequestBody com.pasteleria.dto.AuthRequest req) {
        try {
            var authToken = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword());
            org.springframework.security.core.Authentication auth = authenticationManager.authenticate(authToken);
            org.springframework.security.core.userdetails.UserDetails ud = (org.springframework.security.core.userdetails.UserDetails) auth.getPrincipal();
            String token = jwtUtil.generateToken(ud);
            return ResponseEntity.ok(new com.pasteleria.dto.AuthResponse(token));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null) return ResponseEntity.ok("anonymous");
        return ResponseEntity.ok(authentication.getPrincipal());
    }
}
