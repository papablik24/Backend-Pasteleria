package com.example.backpasteleria.service;

import com.example.backpasteleria.model.Productos;
import com.example.backpasteleria.repository.ProductosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductosService {

    @Autowired
    private ProductosRepository repo;

    public List<Productos> getAll() {
        return repo.findAll();
    }

    public Productos getById(Long id) {
        return repo.findById(id).orElse(null);
    }

    @Transactional // Buena práctica: asegura que si falla algo, no se guarde a medias
    public Productos create(Productos productos) {
        // Validaciones de negocio podrían ir aquí (ej: no permitir precio negativo)
        return repo.save(productos);
    }

    @Transactional
    public Productos update(Long id, Productos productos) {
        Productos existing = getById(id);
        if (existing == null) {
            return null;
        }
        
    
        
        // 1. Datos básicos
        existing.setNombre(productos.getNombre());
        existing.setPrecio(productos.getPrecio());
        existing.setCategoria(productos.getCategoria());
        
        // 2. Datos de inventario y estado (Vistos en Admin-productos.jsx)
        existing.setStock(productos.getStock());
        existing.setEstado(productos.getEstado()); // 'disponible', 'agotado', etc.
        existing.setCodigo(productos.getCodigo()); // Importante si permites editar el código visual
        
        // 3. Datos visuales y marketing (Vistos en Ofertas.jsx y Home.jsx)
        existing.setDescripcion(productos.getDescripcion());
        existing.setImagenUrl(productos.getImagenUrl()); // Asegúrate que tu entidad tenga este campo (o setImg)
        existing.setDescuento(productos.getDescuento()); // Vital para que funcionen las ofertas

        return repo.save(existing);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}