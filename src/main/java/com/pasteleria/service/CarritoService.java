package com.pasteleria.service;

import com.pasteleria.model.*;
import com.pasteleria.repository.CarritoRepository;
import com.pasteleria.repository.ProductosRepository;
import com.pasteleria.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductosRepository productRepository;

    public CarritoService(CarritoRepository carritoRepository, UsuarioRepository usuarioRepository, ProductosRepository productRepository) {
        this.carritoRepository = carritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.productRepository = productRepository;
    }

    public Carrito getOrCrearCarrito(String nombreUsuario) {
        Usuarios user = usuarioRepository.findByNombreUsuario(nombreUsuario).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Optional<Carrito> c = carritoRepository.findByUsuario(user);
        return c.orElseGet(() -> {
            Carrito newCart = Carrito.builder().usuario(user).build();
            return carritoRepository.save(newCart);
        });
    }

    @Transactional
    public Carrito anadirAlCarrito(String username, Long productId, int qty) {
        Carrito cart = getOrCrearCarrito(username);
        Productos product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        // find existing item
        var existing = cart.getItems().stream().filter(i -> i.getProducto().getId().equals(productId)).findFirst();
        if (existing.isPresent()) {
            existing.get().setCantidad(existing.get().getCantidad() + qty);
        } else {
            CarritoItems ci = CarritoItems.builder().carrito(cart).producto(product).cantidad(qty).build();
            cart.getItems().add(ci);
        }
        return carritoRepository.save(cart);
    }

    @Transactional
    public Carrito removerDelCarrito(String username, Long productId) {
        Carrito cart = getOrCrearCarrito(username);
        cart.getItems().removeIf(i -> i.getProducto().getId().equals(productId));
        return carritoRepository.save(cart);
    }

    @Transactional
    public void limpiarCarrito(Carrito cart) {
        cart.getItems().clear();
        carritoRepository.save(cart);
    }
}
