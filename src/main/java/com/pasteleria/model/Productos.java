package com.pasteleria.model;

import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "Productos", description = "Entidad que representa un producto de la pastelería")
public class Productos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- NUEVOS CAMPOS REQUERIDOS POR EL SEEDER ---

    @Schema(description = "Código único del producto (ej: TC001)", example = "TC001")
    @Column(unique = true) // Importante: el código no debería repetirse
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    // Aumentamos la longitud porque las descripciones son largas
    @Column(length = 1000) 
    private String descripcion;

    @Column(nullable = false)
    private Double precio;

    private Integer stock;

    // Mapeado desde 'img' en tu array de frontend
    @Column(name = "imagen_url")
    private String imagenUrl; 

    // 'disponible', 'agotado', etc.
    private String estado;

    // Porcentaje de descuento (ej: 10 para 10%)
    private Integer descuento;

    // --- RELACIÓN CON CATEGORÍA ---
    @ManyToOne(fetch = FetchType.EAGER) // Para que al pedir el producto, venga con su categoría
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
}