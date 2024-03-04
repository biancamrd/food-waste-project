package com.api.licenta.model;

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
@Table(name = "recipe")
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id")
    private Long recipeId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonSerialize(using = UserSerializer.class)
    private User user;

    @Column(nullable = true)
    private String image;

    @Column(nullable = false)
    private String title;

    @Column(name = "ready_in_minutes", nullable = false)
    private int readyInMinutes;

    @Column(nullable = false)
    private int servings;

    @Column(name = "very_healthy", nullable = false)
    private boolean veryHealthy;

    @Column(nullable = false)
    private boolean vegetarian;

    @Column(nullable = true)
    @Lob
    private String instructions;
}
