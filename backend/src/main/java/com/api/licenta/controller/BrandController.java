package com.api.licenta.controller;

import com.api.licenta.model.Brand;
import com.api.licenta.model.Recipe;
import com.api.licenta.service.impl.BrandServiceImpl;
import com.api.licenta.service.interfaces.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class BrandController {
    @Autowired
    private BrandService brandService;

    @RequestMapping("/brands")
    @GetMapping()
    public List <Brand> findAll() {
        return brandService.findAll();
    }

    @GetMapping("brands/id/{id}")
    public ResponseEntity<Brand> getBrandById(@PathVariable Long id) {
        Optional<Brand> brand = brandService.findById(id);
        if (brand.isPresent()) {
            return new ResponseEntity<>(brand.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("brands/{name}")
    public ResponseEntity<Brand> getBrandByName(@PathVariable String name) {
        String lowercaseName = name.toLowerCase();

        Optional<Brand> brand = Optional.ofNullable(brandService.findByName(lowercaseName));
        if (brand.isPresent()) {
            return new ResponseEntity<>(brand.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/brands")
    public ResponseEntity<Brand> createBrand(@RequestBody Brand brand) {
        Brand savedBrand = brandService.save(brand);
        return new ResponseEntity<>(savedBrand, HttpStatus.CREATED);
    }
}
