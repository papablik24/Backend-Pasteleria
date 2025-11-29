package com.pasteleria.model;

import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "orden_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "OrdenItem", description = "Item de una orden: producto, cantidad y precio")
public class OrdenItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "orden_id")
    @JsonBackReference
    private Orden orden;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Productos producto;

    private Integer cantidad;

    private Double precio;
}
