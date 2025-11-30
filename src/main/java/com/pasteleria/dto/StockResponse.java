package com.pasteleria.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockResponse {
    private Long id;
    private String nombre;
    private Integer stock;
    private String estado;
    private Double precio;
}
