package com.api.licenta.repository;

import com.api.licenta.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByBrand(Brand brand);
}
