package com.pasteleria.controller;

import com.pasteleria.model.Productos;
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

    public ProductosController(ProductosRepository productosRepository) {
        this.productosRepository = productosRepository;
    }

    @GetMapping
    @Operation(summary = "Listar productos", description = "Devuelve la lista de productos disponibles")
    public List<Productos> all() {
        return productosRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto", description = "Devuelve un producto por su id")
    public ResponseEntity<Productos> getById(@PathVariable Long id) {
        return productosRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    // @PreAuthorize("hasRole('ADMIN')") // Descomentar si usas @EnableMethodSecurity
    @Operation(summary = "Crear producto", description = "Crea un producto nuevo (solo ADMIN)")
    @ApiResponse(responseCode = "200", description = "Producto creado", content = @Content(schema = @Schema(implementation = Productos.class)))
    public Productos create(@RequestBody Productos producto) {
        // Al guardar, JPA gestionará la relación con Categoría automáticamente
        return productosRepository.save(producto);
    }

    @PutMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')") // Descomentar si usas @EnableMethodSecurity
    @Operation(summary = "Actualizar producto", description = "Actualiza un producto existente (solo ADMIN)")
    public ResponseEntity<Productos> update(@PathVariable Long id, @RequestBody Productos p) {
        return productosRepository.findById(id).map(existing -> {
            // 1. Campos Básicos
            existing.setNombre(p.getNombre());
            existing.setDescripcion(p.getDescripcion());
            existing.setPrecio(p.getPrecio());
            existing.setStock(p.getStock());

            // 2. NUEVOS CAMPOS (Crucial para el Frontend y Seeder)
            existing.setCodigo(p.getCodigo());
            existing.setImagenUrl(p.getImagenUrl()); // Mapea 'img' del front a 'imagenUrl'
            existing.setEstado(p.getEstado());       // 'disponible', 'agotado'
            existing.setDescuento(p.getDescuento()); // Ofertas
            existing.setCategoria(p.getCategoria()); // Relación BD

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