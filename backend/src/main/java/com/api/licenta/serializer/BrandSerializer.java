package com.api.licenta.serializer;

import com.api.licenta.model.Brand;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.hibernate.proxy.HibernateProxy;

import java.io.IOException;

public class BrandSerializer extends JsonSerializer<Brand> {
    @Override
    public void serialize(Brand brand, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (brand instanceof HibernateProxy) {
            brand = (Brand) ((HibernateProxy) brand).getHibernateLazyInitializer().getImplementation();
        }
        gen.writeStartObject();
        gen.writeNumberField("id", brand.getId());
        gen.writeEndObject();
    }
}

