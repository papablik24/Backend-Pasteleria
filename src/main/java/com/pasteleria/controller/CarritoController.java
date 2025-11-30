package com.pasteleria.controller;

import com.pasteleria.dto.CarritoDTO;
import com.pasteleria.dto.CarritoItemDTO;
import com.pasteleria.model.Carrito;
import com.pasteleria.service.CarritoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/carrito")
@Tag(name = "Carrito", description = "Operaciones sobre el carrito de compra")
public class CarritoController {

    private final CarritoService carritoService;
    
    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    private CarritoDTO toDTO(Carrito carrito) {
        if (carrito == null) {
            return CarritoDTO.builder().items(new java.util.ArrayList<>()).build();
        }
        
        return CarritoDTO.builder()
            .id(carrito.getId())
            .usuarioId(carrito.getUsuario() != null ? carrito.getUsuario().getId() : null)
            .items(carrito.getItems() != null ? carrito.getItems().stream()
                .map(item -> CarritoItemDTO.builder()
                    .id(item.getId())
                    .productoId(item.getProducto() != null ? item.getProducto().getId() : null)
                    .productoNombre(item.getProducto() != null ? item.getProducto().getNombre() : "")
                    .productoPrecio(item.getProducto() != null ? item.getProducto().getPrecio() : 0.0)
                    .productoImagen(item.getProducto() != null ? item.getProducto().getImagenUrl() : "")
                    .cantidad(item.getCantidad())
                    .build())
                .collect(Collectors.toList()) : new java.util.ArrayList<>())
            .build();
    }

    @GetMapping
    @Operation(summary = "Obtener carrito", description = "Devuelve el carrito del usuario autenticado")
    public ResponseEntity<CarritoDTO> myCart(@AuthenticationPrincipal UserDetails ud) {
        if (ud == null) {
            System.out.println("❌ Usuario no autenticado intentando acceder al carrito");
            return ResponseEntity.status(401).build();
        }
        System.out.println("✅ Usuario autenticado accediendo al carrito: " + ud.getUsername());
        Carrito carrito = carritoService.getOrCrearCarrito(ud.getUsername());
        return ResponseEntity.ok(toDTO(carrito));
    }

    @PostMapping("/anadir")
    @Operation(summary = "Agregar producto al carrito", description = "Añade una cantidad del producto al carrito")
    public ResponseEntity<CarritoDTO> add(@AuthenticationPrincipal UserDetails ud, 
                                          @RequestParam Long productId, 
                                          @RequestParam int qty) {
        if (ud == null) {
            System.out.println("❌ Usuario no autenticado intentando añadir al carrito");
            return ResponseEntity.status(401).build();
        }
        System.out.println("✅ Usuario " + ud.getUsername() + " añadiendo producto " + productId + " al carrito");
        Carrito carrito = carritoService.anadirAlCarrito(ud.getUsername(), productId, qty);
        System.out.println("✅ Carrito actualizado con " + carrito.getItems().size() + " items");
        return ResponseEntity.ok(toDTO(carrito));
    }

    @DeleteMapping("/remover")
    @Operation(summary = "Remover producto del carrito", description = "Elimina un producto del carrito del usuario")
    public ResponseEntity<CarritoDTO> remove(@AuthenticationPrincipal UserDetails ud, 
                                             @RequestParam Long productId) {
        if (ud == null) {
            System.out.println("❌ Usuario no autenticado intentando remover del carrito");
            return ResponseEntity.status(401).build();
        }
        System.out.println("✅ Usuario " + ud.getUsername() + " removiendo producto " + productId + " del carrito");
        Carrito carrito = carritoService.removerDelCarrito(ud.getUsername(), productId);
        return ResponseEntity.ok(toDTO(carrito));
    }

    @DeleteMapping("/limpiar")
    @Operation(summary = "Limpiar carrito", description = "Elimina todos los productos del carrito del usuario")
    public ResponseEntity<String> clear(@AuthenticationPrincipal UserDetails ud) {
        if (ud == null) {
            System.out.println("❌ Usuario no autenticado intentando limpiar el carrito");
            return ResponseEntity.status(401).build();
        }
        System.out.println("✅ Usuario " + ud.getUsername() + " limpiando el carrito completo");
        Carrito carrito = carritoService.getOrCrearCarrito(ud.getUsername());
        carritoService.limpiarCarrito(carrito);
        return ResponseEntity.ok("Carrito limpiado exitosamente");
    }
}