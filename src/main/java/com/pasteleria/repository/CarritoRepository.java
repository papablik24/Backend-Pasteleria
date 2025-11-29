package com.pasteleria.repository;

import com.pasteleria.model.Carrito;
import com.pasteleria.model.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    Optional<Carrito> findByUsuario(Usuarios usuario);
}
