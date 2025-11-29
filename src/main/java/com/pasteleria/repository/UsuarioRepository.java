package com.pasteleria.repository;

import com.pasteleria.model.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuarios, Long> {
    Optional<Usuarios> findByNombreUsuario(String nombreUsuario);
}
