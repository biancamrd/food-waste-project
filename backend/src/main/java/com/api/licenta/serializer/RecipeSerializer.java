package com.api.licenta.serializer;

import com.api.licenta.model.Recipe;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.hibernate.proxy.HibernateProxy;

import java.io.IOException;

public class RecipeSerializer extends JsonSerializer<Recipe> {
    @Override
    public void serialize(Recipe recipe, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (recipe instanceof HibernateProxy) {
            recipe = (Recipe) ((HibernateProxy) recipe).getHibernateLazyInitializer().getImplementation();
        }
        gen.writeStartObject();
        gen.writeNumberField("recipeId", recipe.getRecipeId());
        gen.writeEndObject();
    }
}

