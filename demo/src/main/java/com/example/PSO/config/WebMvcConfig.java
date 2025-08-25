package com.example.PSO.config;

import com.google.gson.FormattingStyle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.gson.GsonProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.EncoderHttpMessageWriter;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.util.List;

@Configuration
@EnableWebFlux
public class WebMvcConfig implements WebFluxConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }

//    @Override
//    public void addViewControllers(final ViewControllerRegistry registry) {
//        // Forward all routes to index.html except for API routes
//        registry.addViewController("^(?!\\/api).*")
//                .setViewName("forward:/index.html");
//    }

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .serializeNulls()
                .setFormattingStyle(FormattingStyle.COMPACT)
                .create();
    }

    @Override
    public void configureHttpMessageCodecs(@NotNull ServerCodecConfigurer configurer) {
//        GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
//        Gson gson = gson();
//        gsonHttpMessageConverter.setGson(gson);
//        configurer.customCodecs().register(new EncoderHttpMessageWriter<>(new GsonNdjsonEncoder(gson)));
        configurer.defaultCodecs().configureDefaultCodec(codec -> {
            if (codec instanceof GsonHttpMessageConverter) {
                ((GsonHttpMessageConverter) codec).setGson(gson());
            }
        });
    }


}
