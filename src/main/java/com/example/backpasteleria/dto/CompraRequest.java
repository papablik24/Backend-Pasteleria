package com.example.backpasteleria.dto;

import java.util.List;
import lombok.Data;

@Data
public class CompraRequest {
    // Datos del cliente
    private String nombre;
    private String correo;
    private String calle;
    private String comuna;
    private String region;
    // Lista de productos
    private List<ProductoItem> items;

        @Data
        public static class ProductoItem {
        private Long productoId;
        private Integer cantidad;
        private Double precio;
    }
}
