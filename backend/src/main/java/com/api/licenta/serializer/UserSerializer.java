package com.api.licenta.serializer;

import com.api.licenta.model.User;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.hibernate.proxy.HibernateProxy;
import java.io.IOException;


public class UserSerializer extends JsonSerializer<User> {
    @Override
    public void serialize(User user, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (user instanceof HibernateProxy) {
            user = (User) ((HibernateProxy) user).getHibernateLazyInitializer().getImplementation();
        }
        gen.writeStartObject();
        gen.writeNumberField("id", user.getId());
        gen.writeStringField("lastname", user.getLastname());
        gen.writeStringField("firstname", user.getFirstname());
        gen.writeStringField("email", user.getEmail());
        gen.writeStringField("password", user.getPassword());
        gen.writeEndObject();
    }
}