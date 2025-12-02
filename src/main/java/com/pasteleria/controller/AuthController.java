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
            System.out.println("🔑 Intento de login - Usuario: " + req.getUsername());
            var authToken = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword());
            org.springframework.security.core.Authentication auth = authenticationManager.authenticate(authToken);
            org.springframework.security.core.userdetails.UserDetails ud = (org.springframework.security.core.userdetails.UserDetails) auth.getPrincipal();
            String token = jwtUtil.generateToken(ud);
            System.out.println("✅ Login exitoso para: " + req.getUsername());
            return ResponseEntity.ok(new com.pasteleria.dto.AuthResponse(token));
        } catch (Exception e) {
            System.out.println("❌ Error en login - Usuario: " + req.getUsername() + " - Error: " + e.getMessage());
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Obtener usuario actual", description = "Devuelve la información del usuario autenticado")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null) return ResponseEntity.ok("anonymous");
        return ResponseEntity.ok(authentication.getPrincipal());
    }

    @GetMapping("/usuarios")
    @Operation(summary = "Listar todos los usuarios", description = "Devuelve la lista completa de usuarios registrados")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios")
    public ResponseEntity<?> getAllUsers() {
        var usuarios = userService.findAll();
        // Ocultar contraseñas por seguridad
        usuarios.forEach(u -> u.setContrasena(null));
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/usuarios/{id}")
    @Operation(summary = "Obtener usuario por ID", description = "Devuelve la información de un usuario específico")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        var usuario = userService.findById(id);
        if (usuario.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        usuario.get().setContrasena(null);
        return ResponseEntity.ok(usuario.get());
    }

    @PutMapping("/usuarios/{id}")
    @Operation(summary = "Actualizar usuario", description = "Actualiza la información de un usuario existente")
    @ApiResponse(responseCode = "200", description = "Usuario actualizado", content = @Content(schema = @Schema(implementation = Usuarios.class)))
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Usuarios usuario) {
        try {
            Usuarios updated = userService.update(id, usuario);
            updated.setContrasena(null);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/admin/usuarios")
    @Operation(summary = "Registrar usuario (Admin)", description = "Permite al administrador registrar un nuevo usuario con cualquier rol")
    @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente", content = @Content(schema = @Schema(implementation = Usuarios.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario ya existe")
    public ResponseEntity<?> registerUserByAdmin(@RequestBody Usuarios usuario) {
        // Validar campos requeridos
        if (usuario.getNombreUsuario() == null || usuario.getNombreUsuario().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El nombre de usuario es requerido");
        }
        if (usuario.getContrasena() == null || usuario.getContrasena().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("La contraseña es requerida");
        }
        
        // Verificar si el usuario ya existe
        if (userService.findByNombreUsuario(usuario.getNombreUsuario()).isPresent()) {
            return ResponseEntity.badRequest().body("El nombre de usuario ya existe");
        }
        
        // Si no se especifica rol, asignar 'cliente' por defecto
        if (usuario.getRol() == null || usuario.getRol().trim().isEmpty()) {
            usuario.setRol("cliente");
        }
        
        // Registrar el usuario
        Usuarios usuarioGuardado = userService.register(usuario);
        
        // No devolver la contraseña en la respuesta
        usuarioGuardado.setContrasena(null);
        
        return ResponseEntity.ok(usuarioGuardado);
    }

    @PutMapping("/admin/usuarios/{id}")
    @Operation(summary = "Editar usuario (Admin)", description = "Permite al administrador editar un usuario existente")
    @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente", content = @Content(schema = @Schema(implementation = Usuarios.class)))
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<?> updateUserByAdmin(@PathVariable Long id, @RequestBody Usuarios usuario) {
        try {
            Usuarios usuarioActualizado = userService.update(id, usuario);
            usuarioActualizado.setContrasena(null);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Usuario no encontrado con id: " + id);
        }
    }

    @DeleteMapping("/admin/usuarios/{id}")
    @Operation(summary = "Eliminar usuario (Admin)", description = "Permite al administrador eliminar un usuario existente")
    @ApiResponse(responseCode = "200", description = "Usuario eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<?> deleteUserByAdmin(@PathVariable Long id) {
        try {
            userService.delete(id);
            return ResponseEntity.ok("Usuario eliminado exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Usuario no encontrado con id: " + id);
        }
    }
}
