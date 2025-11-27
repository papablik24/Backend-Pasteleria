package com.example.backpasteleria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "productos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Productos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private Integer codigo;
    private String nombre;
    @NotNull
    private Double precio;
    private String descripcion;
    private Integer stock;
    private String estado;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    // URL de la imagen del producto (opcional)
    private String imagenUrl;

    // Porcentaje o valor de descuento aplicado al producto (opcional)
    private Double descuento;

    
}
