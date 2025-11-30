package com.pasteleria.service;

import com.pasteleria.model.*;
import com.pasteleria.repository.OrdenItemRepository;
import com.pasteleria.repository.OrdenRepository;
import com.pasteleria.repository.ProductosRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class OrdenService {

    private final OrdenRepository ordenRepository;
    private final OrdenItemRepository ordenItemRepository;
    private final ProductosRepository productosRepository;
    private final CarritoService carritoService;

    public OrdenService(OrdenRepository ordenRepository, OrdenItemRepository ordenItemRepository, ProductosRepository productosRepository, CarritoService carritoService) {
        this.ordenRepository = ordenRepository;
        this.ordenItemRepository = ordenItemRepository;
        this.productosRepository = productosRepository;
        this.carritoService = carritoService;
    }

    @Transactional
    public Orden checkout(String nombreUsuario) {
        Carrito carrito = carritoService.getOrCrearCarrito(nombreUsuario);
        if (carrito.getItems().isEmpty()) throw new RuntimeException("Carrito vacío");
        
        System.out.println("🛒 CHECKOUT - Usuario: " + nombreUsuario);
        System.out.println("🛒 Items en carrito: " + carrito.getItems().size());
        
        // verify stock and compute total
        double total = 0.0;
        for (CarritoItems ci : carrito.getItems()) {
            Productos p = productosRepository.findById(ci.getProducto().getId()).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            
            System.out.println("📦 Producto: " + p.getNombre() + 
                             " | Stock disponible: " + p.getStock() + 
                             " | Cantidad en carrito: " + ci.getCantidad());
            
            if (p.getStock() == null || p.getStock() < ci.getCantidad()) {
                String errorMsg = "Stock insuficiente para producto: " + p.getNombre() + 
                                " (Disponible: " + p.getStock() + ", Solicitado: " + ci.getCantidad() + ")";
                System.out.println("❌ " + errorMsg);
                throw new RuntimeException(errorMsg);
            }
            total += p.getPrecio() * ci.getCantidad();
        }

        Orden orden = Orden.builder()
                .usuario(carrito.getUsuario())
                .total(total)
                .createdAt(Instant.now())
                .build();

        for (CarritoItems ci : carrito.getItems()) {
            Productos p = productosRepository.findById(ci.getProducto().getId()).get();
            // decrement stock
            p.setStock(p.getStock() - ci.getCantidad());
            productosRepository.save(p);

            OrdenItems oi = OrdenItems.builder()
                    .orden(orden)
                    .producto(p)
                    .cantidad(ci.getCantidad())
                    .precio(p.getPrecio())
                    .build();
            orden.getItems().add(oi);
        }

        Orden saved = ordenRepository.save(orden);
        // limpiar carrito
        carritoService.limpiarCarrito(carrito);

        return saved;
    }
}
