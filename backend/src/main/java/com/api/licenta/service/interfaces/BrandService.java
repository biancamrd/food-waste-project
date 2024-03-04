package com.api.licenta.service.interfaces;

import com.api.licenta.model.Brand;
import com.api.licenta.model.RecipeIngredient;

import java.util.List;
import java.util.Optional;

public interface BrandService {
    List<Brand> findAll();

    Optional<Brand> findById(Long id);

    Brand save(Brand brand);
    Brand findByName(String name);
}
