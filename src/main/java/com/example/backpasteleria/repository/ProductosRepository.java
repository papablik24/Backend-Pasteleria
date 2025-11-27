package com.example.backpasteleria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.backpasteleria.model.Productos;

@Repository
public interface ProductosRepository extends JpaRepository<Productos, Long> {

}
