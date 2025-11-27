package com.example.backpasteleria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backpasteleria.model.Boleta;

@Repository
public interface BoletaRepository extends JpaRepository<Boleta, Long> {

    
}
