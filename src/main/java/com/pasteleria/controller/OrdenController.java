package com.pasteleria.controller;

import com.pasteleria.model.Orden;
import com.pasteleria.repository.OrdenRepository;
import com.pasteleria.service.OrdenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/ordenes")
@Tag(name = "ordenes", description = "Gestión de órdenes y checkout")
public class OrdenController {

    private final OrdenService ordenService;
    private final OrdenRepository ordenRepository;

    public OrdenController(OrdenService ordenService, OrdenRepository ordenRepository) {
        this.ordenService = ordenService;
        this.ordenRepository = ordenRepository;
    }

    @PostMapping("/checkout")
    @PreAuthorize("hasAuthority('cliente') or hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    @Operation(summary = "Checkout", description = "Realiza el pago/checkout del carrito del usuario autenticado")
    @ApiResponse(responseCode = "200", description = "Orden creada", content = @Content(schema = @Schema(implementation = Orden.class)))
    public ResponseEntity<?> checkout(@AuthenticationPrincipal UserDetails ud) {
        if (ud == null) {
            System.out.println("❌ Usuario no autenticado intentando hacer checkout");
            return ResponseEntity.status(401).body("Usuario no autenticado");
        }
        System.out.println("✅ Usuario: " + ud.getUsername());
        System.out.println("✅ Authorities: " + ud.getAuthorities());
        ud.getAuthorities().forEach(auth -> System.out.println("   - Authority: " + auth.getAuthority()));
        
        try {
            Orden orden = ordenService.checkout(ud.getUsername());
            return ResponseEntity.ok(orden);
        } catch (Exception e) {
            System.out.println("❌ Error en checkout: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_VENDOR')")
    @Operation(summary = "Listar órdenes", description = "Lista todas las órdenes (ADMIN, VENDOR)")
    public ResponseEntity<?> listAll() {
        List<Orden> all = ordenRepository.findAll();
        return ResponseEntity.ok(all);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_VENDOR') or hasAuthority('cliente') or hasAuthority('ROLE_USER')")
    @Operation(summary = "Detalle de orden", description = "Devuelve detalle de la orden. Clientes solo pueden ver sus órdenes")
    public ResponseEntity<?> getById(@PathVariable Long id, @AuthenticationPrincipal UserDetails ud) {
        return ordenRepository.findById(id).map(orden -> {
            // if client, ensure ownership
            if (ud.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_VENDOR"))) {
                if (!orden.getUsuario().getNombreUsuario().equals(ud.getUsername())) return ResponseEntity.status(403).build();
            }
            return ResponseEntity.ok(orden);
        }).orElse(ResponseEntity.notFound().build());
    }
}
