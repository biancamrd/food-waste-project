package com.api.licenta.model;

import com.api.licenta.serializer.UserSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ingredient")
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonSerialize(using = UserSerializer.class)
    private User user;
    @Column(nullable = false)
    private String name;
    @Column(nullable = true)
    private Float quantity;
    @Column(nullable = false)
    private String unit;
    @Column(nullable = true)
    @Temporal(TemporalType.DATE)
    private Date expirationDate;
}
