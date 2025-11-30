package com.pasteleria.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarritoItemDTO {
    private Long id;
    private Long productoId;
    private String productoNombre;
    private Double productoPrecio;
    private String productoImagen;
    private Integer cantidad;
}