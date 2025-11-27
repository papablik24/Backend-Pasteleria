package com.example.backpasteleria.controller;

import com.example.backpasteleria.dto.CompraRequest;
import com.example.backpasteleria.model.*;
import com.example.backpasteleria.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/boletas")
@CrossOrigin(origins = "*")
public class BoletaController {

    @Autowired private BoletaRepository boletaRepo;
    @Autowired private DetalleBoletaRepository detalleRepo;
    @Autowired private ProductosRepository prodRepo;

    @PostMapping
    @Transactional // LÓGICA DE NEGOCIO CRÍTICA (IE3.1.1)
    public ResponseEntity<?> crearBoleta(@RequestBody CompraRequest req) {
        Boleta boleta = new Boleta();
        boleta.setFecha(LocalDateTime.now());
        boleta.setEstado("pagado");
        boleta.setCalle(req.getCalle()); // Guardamos dirección
        boleta = boletaRepo.save(boleta);

        double total = 0;
        List<DetalleBoleta> detalles = new ArrayList<>();

        for (CompraRequest.ProductoItem item : req.getItems()) {
            Productos p = prodRepo.findById(item.getProductoId()).orElseThrow();
            
            // Lógica de validación de stock
            if (p.getStock() < item.getCantidad()) {
                throw new RuntimeException("Sin stock para: " + p.getNombre());
            }
            // Lógica de descuento
            p.setStock(p.getStock() - item.getCantidad());
            if(p.getStock() == 0) p.setEstado("agotado");
            prodRepo.save(p);

            DetalleBoleta d = new DetalleBoleta();
            d.setBoleta(boleta);
            d.setProducto(p);
            d.setCantidad(item.getCantidad());
            d.setPrecioUnitario(item.getPrecio());
            detalles.add(d);
            total += (item.getCantidad() * item.getPrecio());
        }
        detalleRepo.saveAll(detalles);
        
        boleta.setTotal(total);
        boletaRepo.save(boleta);
        
        return ResponseEntity.ok("Boleta creada ID: " + boleta.getId());
    }
}