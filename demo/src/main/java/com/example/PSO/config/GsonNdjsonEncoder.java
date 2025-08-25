package com.example.PSO.config;

import com.google.gson.Gson;
import feign.RequestTemplate;
import feign.gson.GsonEncoder;

import java.lang.reflect.Type;

public class GsonNdjsonEncoder extends GsonEncoder {
    private final Gson gson;

    public GsonNdjsonEncoder(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) {
        String json = gson.toJson(object) + "\n";
        template.body(json);
    }
}