package com.api.licenta.model;
import com.api.licenta.serializer.BrandSerializer;
import com.api.licenta.serializer.UserSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "BrandID", nullable = false)
    @JsonSerialize(using = BrandSerializer.class)
    private Brand brand;

    @Column(nullable = false)
    private String name;
}
