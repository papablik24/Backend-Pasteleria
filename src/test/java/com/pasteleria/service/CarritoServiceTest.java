package com.pasteleria.service;

import com.pasteleria.model.Carrito;
import com.pasteleria.model.CarritoItems;
import com.pasteleria.model.Productos;
import com.pasteleria.model.Usuarios;
import com.pasteleria.repository.CarritoRepository;
import com.pasteleria.repository.ProductosRepository;
import com.pasteleria.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @Mock
    CarritoRepository cartRepository;
    @Mock
    UsuarioRepository userRepository;
    @Mock
    ProductosRepository productRepository;

    @InjectMocks
    CarritoService cartService;

    @Test
    void getOrCreateCart_existing() {
        Usuarios usuarios = new Usuarios(1L, "u", "p", "ROL_CLIENTE", null);
        Carrito carrito = Carrito.builder().id(1L).usuario(usuarios).items(new ArrayList<>()).build();

        when(userRepository.findByNombreUsuario("u")).thenReturn(Optional.of(usuarios));
        when(cartRepository.findByUsuario(usuarios)).thenReturn(Optional.of(carrito));
        Carrito result = cartService.getOrCrearCarrito("u");
        assertThat(result).isSameAs(carrito);
        verify(cartRepository, never()).save(any());
    }

    @Test
    void addToCart_newItem() {
        Usuarios usuarios = new Usuarios(1L, "u", "p", "ROLE_CLIENT", null);
        Carrito carrito = Carrito.builder().id(1L).usuario(usuarios).items(new ArrayList<>()).build();
        Productos productos = Productos.builder().id(2L).nombre("Prod").precio(10.0).stock(5).build();

        when(userRepository.findByNombreUsuario("u")).thenReturn(Optional.of(usuarios));
        when(cartRepository.findByUsuario(usuarios)).thenReturn(Optional.of(carrito));
        when(productRepository.findById(2L)).thenReturn(Optional.of(productos));
        when(cartRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Carrito updated = cartService.anadirAlCarrito("u", 2L, 2);
        assertThat(updated.getItems()).hasSize(1);
        CarritoItems ci = updated.getItems().get(0);
        assertThat(ci.getProducto().getId()).isEqualTo(2L);
        assertThat(ci.getCantidad()).isEqualTo(2);
        verify(cartRepository).save(carrito);
    }

    @Test
    void removeFromCart_removesItem() {
        Usuarios usuarios = new Usuarios(1L, "u", "p", "ROLE_CLIENT", null);
        Productos productos = Productos.builder().id(2L).nombre("Prod").precio(10.0).stock(5).build();
        CarritoItems ci = CarritoItems.builder().id(1L).producto(productos).cantidad(1).build();
        Carrito carrito = Carrito.builder().id(1L).usuario(usuarios).items(new ArrayList<>()).build();
        carrito.getItems().add(ci);

        when(userRepository.findByNombreUsuario("u")).thenReturn(Optional.of(usuarios));
        when(cartRepository.findByUsuario(usuarios)).thenReturn(Optional.of(carrito));
        when(cartRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Carrito after = cartService.removerDelCarrito("u", 2L);
        assertThat(after.getItems()).isEmpty();
        verify(cartRepository).save(carrito);
    }
}
