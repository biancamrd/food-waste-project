package com.api.licenta.service.interfaces;

import com.api.licenta.model.Brand;
import com.api.licenta.model.Product;
import com.api.licenta.model.Recipe;
import com.api.licenta.model.RecipeIngredient;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Optional<Product> findById(Long id);

    List<Product> findAllByBrand(Brand brand);

    Product save(Product product);
}
