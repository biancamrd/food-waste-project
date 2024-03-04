package com.api.licenta.controller;

import com.api.licenta.model.*;
import com.api.licenta.service.impl.RecipeIngredientServiceImpl;
import com.api.licenta.service.interfaces.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.findById(id);
        if (product.isPresent()) {
            return new ResponseEntity<>(product.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/products/brands/{brandId}")
    public ResponseEntity<List<Product>> getProductsByBrandId(@PathVariable Long brandId) {
        Brand brand = new Brand();
        brand.setId(brandId);
        List<Product> products = productService.findAllByBrand(brand);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }


    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product savedProduct = productService.save(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

}
