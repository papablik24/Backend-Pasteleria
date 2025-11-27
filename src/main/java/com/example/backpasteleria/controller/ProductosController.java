package com.example.backpasteleria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backpasteleria.service.ProductosService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.example.backpasteleria.model.Productos;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;


@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
@Tag(name = "Controlador de los Productos", description = "Operaciones CRUD para el catálogo de la Pastelería")
public class ProductosController {

    @Autowired
    private ProductosService productosService;

    @Operation(summary = "Listar productos", description = "Obtiene todo el catálogo de Pasteles disponible")
    @GetMapping
    public ResponseEntity<List<Productos>> list() {
        return ResponseEntity.ok(productosService.getAll());
    }
    
    @Operation(summary = "Obtener por ID", description = "Busca un producto específico")
    @GetMapping("/{id}")
    public ResponseEntity<Productos> get(@PathVariable Long id) {
        Productos producto = productosService.getById(id);
        if (producto != null) {
            return ResponseEntity.ok(producto);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Crear producto", description = "Solo permitido para Administradores")
    @PostMapping
    public ResponseEntity<Productos> create(@RequestBody Productos productos) {
        Productos nuevo = productosService.create(productos);
        // Retorna código 201 Created cuando se crea un recurso
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @Operation(summary = "Actualizar producto", description = "Modifica los datos de un producto ya existente")
    @PutMapping("/{id}")
    public ResponseEntity<Productos> update(@PathVariable Long id, @RequestBody Productos productos) {
        Productos actualizado = productosService.update(id, productos);
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar producto", description = "Borra un producto del sistema")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productosService.delete(id);
        // Retorna 204 No Content cuando se elimina un recurso
        return ResponseEntity.noContent().build();
    }

    
    
}
