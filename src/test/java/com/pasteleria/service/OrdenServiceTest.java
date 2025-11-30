package com.pasteleria.service;

import com.pasteleria.model.*;
import com.pasteleria.repository.OrdenItemRepository;
import com.pasteleria.repository.OrdenRepository;
import com.pasteleria.repository.ProductosRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdenServiceTest {

    @Mock
    OrdenRepository ordenRepository;
    @Mock
    OrdenItemRepository ordenItemRepository;
    @Mock
    ProductosRepository productosRepository;
    @Mock
    CarritoService carritoService;

    @InjectMocks
    OrdenService ordenService;

    @Test
    void checkout_success() {
        Usuarios usuarios = new Usuarios(1L, "u", "p", "ROLE_CLIENT", null);
        Productos productos = Productos.builder().id(2L).nombre("T").precio(5.0).stock(10).build();
        CarritoItems ci = CarritoItems.builder().producto(productos).cantidad(2).build();
        Carrito carrito = Carrito.builder().id(1L).usuario(usuarios).items(new ArrayList<>()).build();
        carrito.getItems().add(ci);

        when(carritoService.getOrCrearCarrito("u")).thenReturn(carrito);
        when(productosRepository.findById(2L)).thenReturn(Optional.of(productos));
        when(ordenRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ordenService.checkout("u");

        // verify stock decreased
        verify(productosRepository, times(1)).save(argThat(prod -> prod.getStock() == 8));
        verify(ordenRepository).save(any(Orden.class));
        verify(carritoService).limpiarCarrito(carrito);
    }

    @Test
    void checkout_insufficientStock_throws() {
        Usuarios usuarios = new Usuarios(1L, "u", "p", "ROL_CLIENT", null);
        Productos productos = Productos.builder().id(2L).nombre("T").precio(5.0).stock(1).build();
        CarritoItems ci = CarritoItems.builder().producto(productos).cantidad(2).build();
        Carrito carrito = Carrito.builder().id(1L).usuario(usuarios).items(new ArrayList<>()).build();
        carrito.getItems().add(ci);
        when(carritoService.getOrCrearCarrito("u")).thenReturn(carrito);
        when(productosRepository.findById(2L)).thenReturn(Optional.of(productos));

        assertThatThrownBy(() -> ordenService.checkout("u")).isInstanceOf(RuntimeException.class);
        verify(productosRepository, never()).save(any());
    }
}
