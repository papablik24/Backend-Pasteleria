package com.pasteleria.controller;

import com.pasteleria.dto.StockResponse;
import com.pasteleria.model.Productos;
import com.pasteleria.repository.CategoriaRepository;
import com.pasteleria.repository.ProductosRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@CrossOrigin(origins = "*") // Permite conexión desde React
@RestController
@RequestMapping("/api/productos") // AJUSTE: Coincide con SecurityConfig y DataSeeder
@Tag(name = "Productos", description = "Gestión de productos: CRUD")
public class ProductosController {

    private final ProductosRepository productosRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductosController(ProductosRepository productosRepository, CategoriaRepository categoriaRepository) {
        this.productosRepository = productosRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping
    @Operation(summary = "Listar productos", description = "Devuelve la lista de productos disponibles")
    public List<Productos> all() {
        return productosRepository.findAll();
    }

    @GetMapping("/stock")
    @Operation(summary = "Ver stock de productos", description = "Devuelve la lista de productos con información de stock disponible")
    public ResponseEntity<List<StockResponse>> getStock() {
        List<Productos> productos = productosRepository.findAll();
        List<StockResponse> stockList = productos.stream()
                .map(p -> new StockResponse(
                        p.getId(),
                        p.getNombre(),
                        p.getStock(),
                        p.getEstado(),
                        p.getPrecio()
                ))
                .toList();
        return ResponseEntity.ok(stockList);
    }

    @PatchMapping("/{id}/stock")
    @Operation(summary = "Actualizar stock de producto", description = "Actualiza la cantidad de stock de un producto específico")
    public ResponseEntity<?> updateStock(@PathVariable Long id, @RequestParam Integer cantidad) {
        return productosRepository.findById(id).map(producto -> {
            producto.setStock(cantidad);
            if (cantidad > 0) {
                producto.setEstado("disponible");
            } else {
                producto.setEstado("agotado");
            }
            productosRepository.save(producto);
            return ResponseEntity.ok().body("Stock actualizado: " + producto.getNombre() + " = " + cantidad + " unidades");
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto", description = "Devuelve un producto por su id")
    public ResponseEntity<Productos> getById(@PathVariable Long id) {
        return productosRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    // @PreAuthorize("hasRole('ADMIN')") //
    @Operation(summary = "Crear producto", description = "Crea un producto nuevo (solo ADMIN)")
    @ApiResponse(responseCode = "200", description = "Producto creado", content = @Content(schema = @Schema(implementation = Productos.class)))
    public Productos create(@RequestBody Productos producto) {
        // Al guardar, JPA gestionará la relación con Categoría automáticamente
        return productosRepository.save(producto);
    }

    @PutMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')") // 
    @Operation(summary = "Actualizar producto", description = "Actualiza un producto existente (solo ADMIN)")
    public ResponseEntity<Productos> update(@PathVariable Long id, @RequestBody Productos p) {
        return productosRepository.findById(id).map(existing -> {
            // 1. Campos Básicos
            if (p.getNombre() != null) existing.setNombre(p.getNombre());
            if (p.getDescripcion() != null) existing.setDescripcion(p.getDescripcion());
            if (p.getPrecio() != null) existing.setPrecio(p.getPrecio());
            if (p.getStock() != null) existing.setStock(p.getStock());

            // 2. NUEVOS CAMPOS
            if (p.getCodigo() != null) existing.setCodigo(p.getCodigo());
            if (p.getImagenUrl() != null) existing.setImagenUrl(p.getImagenUrl());
            if (p.getEstado() != null) existing.setEstado(p.getEstado());
            if (p.getDescuento() != null) existing.setDescuento(p.getDescuento());
            
            // 3. CATEGORÍA - Manejar correctamente
            if (p.getCategoria() != null && p.getCategoria().getId() != null) {
                categoriaRepository.findById(p.getCategoria().getId()).ifPresent(existing::setCategoria);
            }

            return ResponseEntity.ok(productosRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')") // Descomentar si usas @EnableMethodSecurity
    @Operation(summary = "Eliminar producto", description = "Elimina un producto por id (solo ADMIN)")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!productosRepository.existsById(id)) return ResponseEntity.notFound().build();
        productosRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}