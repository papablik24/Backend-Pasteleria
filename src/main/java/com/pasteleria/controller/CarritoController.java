package com.pasteleria.controller;

import com.pasteleria.model.Carrito;
import com.pasteleria.service.CarritoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/carrito")
@Tag(name = "Carrito", description = "Operaciones sobre el carrito de compra")
public class CarritoController {

    private final CarritoService carritoService;
    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping
    @Operation(summary = "Obtener carrito", description = "Devuelve el carrito del usuario autenticado")
    public ResponseEntity<?> myCart(@AuthenticationPrincipal UserDetails ud) {
        Carrito carrito = carritoService.getOrCrearCarrito(ud.getUsername());
        return ResponseEntity.ok(carrito);
    }

    @PostMapping("/anadir")
    @Operation(summary = "Agregar producto al carrito", description = "Añade una cantidad del producto al carrito")
    public ResponseEntity<?> add(@AuthenticationPrincipal UserDetails ud, @RequestParam Long productId, @RequestParam int qty) {
        Carrito carrito = carritoService.anadirAlCarrito(ud.getUsername(), productId, qty);
        return ResponseEntity.ok(carrito);
    }

    @PostMapping("/remover")
    @Operation(summary = "Remover producto del carrito", description = "Elimina un producto del carrito del usuario")
    public ResponseEntity<?> remove(@AuthenticationPrincipal UserDetails ud, @RequestParam Long productId) {
        Carrito carrito = carritoService.removerDelCarrito(ud.getUsername(), productId);
        return ResponseEntity.ok(carrito);
    }
}
