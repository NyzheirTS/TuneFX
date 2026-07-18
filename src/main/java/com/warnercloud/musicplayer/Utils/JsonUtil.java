package com.warnercloud.musicplayer.Utils;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

public final class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    private JsonUtil() {}

    public static<T> T fromJson(String json, Class<T> clazz){
        try {
            return mapper.readValue(json, clazz);
        } catch (JacksonException e){
            throw new RuntimeException("Failed to deserialize json: ", e);
        }
    }

    public String toJson(){
        try {
            return mapper.writeValueAsString(this);
        } catch (JacksonException e){
            throw new RuntimeException("Failed to serialize json: ", e);
        }
    }
}
