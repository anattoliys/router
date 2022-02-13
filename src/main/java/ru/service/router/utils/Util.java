package ru.service.router.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class Util {
    public static String toJsonString(Object object) throws JsonProcessingException {
        var objectWriter = new ObjectMapper();
        objectWriter.registerModule(new JavaTimeModule());
        objectWriter.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        return objectWriter.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }

    public static <T> T toObject(String json, Class<T> object) throws JsonProcessingException {
        var objectWriter = new ObjectMapper();
        objectWriter.registerModule(new JavaTimeModule());
        objectWriter.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        return objectWriter.readValue(json, object);
    }
}
