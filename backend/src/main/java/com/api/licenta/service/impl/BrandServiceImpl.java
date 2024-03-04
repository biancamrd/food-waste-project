package com.api.licenta.service.impl;

import com.api.licenta.model.Brand;
import com.api.licenta.repository.BrandRepository;
import com.api.licenta.service.interfaces.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Override
    public List<Brand> findAll() {
        return brandRepository.findAll();
    }

    @Override
    public Optional<Brand> findById(Long id) {
        return brandRepository.findById(id);
    }

    @Override
    public Brand save(Brand brand) {
        return brandRepository.save(brand);
    }

    @Override
    public Brand findByName(String name) {
        return brandRepository.findByName(name);
    }
}
