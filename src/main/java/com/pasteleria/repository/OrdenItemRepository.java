package com.pasteleria.repository;

import com.pasteleria.model.OrdenItems;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenItemRepository extends JpaRepository<OrdenItems, Long> {
}
