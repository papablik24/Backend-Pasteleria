package com.pasteleria.service;

import com.pasteleria.model.Usuarios;
import com.pasteleria.repository.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuarios register(Usuarios user) {
        user.setContrasena(passwordEncoder.encode(user.getContrasena()));
        if (user.getRol() == null) user.setRol("cliente");
        return usuarioRepository.save(user);
    }

    public Optional<Usuarios> findByNombreUsuario(String nombreUsuario) {
        return usuarioRepository.findByNombreUsuario(nombreUsuario);
    }

    public List<Usuarios> findAll() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuarios> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuarios update(Long id, Usuarios usuarioActualizado) {
        Usuarios usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        
        // Actualizar solo los campos que no son nulos
        if (usuarioActualizado.getNombreUsuario() != null) {
            usuario.setNombreUsuario(usuarioActualizado.getNombreUsuario());
        }
        if (usuarioActualizado.getContrasena() != null && !usuarioActualizado.getContrasena().isEmpty()) {
            usuario.setContrasena(passwordEncoder.encode(usuarioActualizado.getContrasena()));
        }
        if (usuarioActualizado.getRol() != null) {
            usuario.setRol(usuarioActualizado.getRol());
        }
        if (usuarioActualizado.getNombre() != null) {
            usuario.setNombre(usuarioActualizado.getNombre());
        }
        
        return usuarioRepository.save(usuario);
    }

    public void delete(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con id: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String usuario) throws UsernameNotFoundException {
        Usuarios usuarios = usuarioRepository.findByNombreUsuario(usuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + usuario));

        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(usuarios.getRol()));
        return new org.springframework.security.core.userdetails.User(usuarios.getNombreUsuario(), usuarios.getContrasena(), authorities);
    }
}
