package com.api.licenta.repository;

import com.api.licenta.model.Brand;
import com.api.licenta.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    List<Brand> findAll();

    Optional<Brand> findById(Long id);

    Brand save(Brand brand);

    Brand findByName(String name);

}
