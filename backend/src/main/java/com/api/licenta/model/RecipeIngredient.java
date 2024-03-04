package com.api.licenta.model;
import com.api.licenta.serializer.RecipeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "recipe_ingredient")
public class RecipeIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_ingredient_id")
    private Long recipeIngredientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    @JsonSerialize(using = RecipeSerializer.class)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Recipe recipe;

    @Column(name = "ingredient_name")
    private String ingredientName;

    private String quantity;

    @Column(name = "unit")
    private String unit;
}
