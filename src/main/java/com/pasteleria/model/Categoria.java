package com.pasteleria.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore; // <--- IMPORTANTE: Importar esto
import java.util.List;

@Entity
@Table(name = "categorias")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;

    // --- AQUÍ ESTÁ LA SOLUCIÓN ---
    @OneToMany(mappedBy = "categoria")
    @JsonIgnore // <--- AGREGA ESTA LÍNEA OBLIGATORIAMENTE
    private List<Productos> productos;
}
