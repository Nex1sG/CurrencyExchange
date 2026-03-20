package main.exchange.input.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperProvider {

    private static final ObjectMapper INSTANCE = new ObjectMapper();

    private ObjectMapperProvider() {}

    public static ObjectMapper getInstance() {
        return INSTANCE;
    }
}

