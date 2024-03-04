package com.api.licenta.service.impl;

import com.api.licenta.model.Brand;
import com.api.licenta.model.Product;
import com.api.licenta.repository.ProductRepository;
import com.api.licenta.repository.RecipeIngredientRepository;
import com.api.licenta.service.interfaces.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public List<Product> findAllByBrand(Brand brand) {
        return productRepository.findAllByBrand(brand);
    }

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }
}
