package com.example.PSO.controller;

import com.example.PSO.model.Group;
import com.example.PSO.model.Image64;
import com.example.PSO.model.ItemGroup;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;

@RestController
public class FrontendController {
    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    Gson gson;

    String filePath = "C:\\Users\\pbursic\\Downloads\\demo\\demo\\src\\main\\resources\\static\\images\\NewFolder\\Beznaslova.jpg";

    List<String> groups = List.of("Pizza", "Burgers", "Sushi", "Desserts", "Pasta", "Salads", "Water", "Juice", "Soda", "Coffee");

    @GetMapping()
    public List<String> index() throws IOException {
        List<String> resources = new LinkedList<>();
        for (int i = 0; i < 60; i++) {
            String filePath = "C:\\Users\\pbursic\\Downloads\\demo\\demo\\src\\main\\resources\\static\\images\\NewFolder\\Beznaslova" + i + ".jpg";
            byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
            String base64Image = Base64.getEncoder().encodeToString(fileContent);
            resources.add("data:image/jpeg;base64," + base64Image);
        }
        return resources;
    }

    @GetMapping("/images/empty")
    public String empty() throws IOException {
        byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
        return "data:image/svg+xml;base64," + Base64.getEncoder().encodeToString(fileContent);
    }


    @GetMapping(path = "/images", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public ResponseEntity<Flux<String>> getImages() {
        final int IMAGE_COUNT = 60;

        Flux<String> flux = Flux.range(0, IMAGE_COUNT)
                .flatMapSequential(this::loadImageReactive)
                .delayElements(Duration.ofMillis(100))
                .filter(Objects::nonNull)
                .doOnError(System.err::println);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_NDJSON)
                .body(flux);
    }

    @GetMapping(value = "/stream-data", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<String> streamData() {
        ItemGroup itemGroup = new ItemGroup(1, "Pakoni", new LinkedList<>(), Group.FOOD);
        System.out.println(gson.toJson(new Image64("Pakoni", 1, itemGroup)));
        return Flux.interval(Duration.ofMillis(500))
                .take(10)
                .doOnNext(obj -> System.out.println("Sending: " + obj))
                .map(i -> "{ \"Item\": " + i + " }\n");
    }


    private Mono<String> loadImageReactive(int index) {
        return Mono.fromCallable(() -> {
                    Resource resource = resourceLoader.getResource("classpath:static/images/NewFolder/Beznaslova" + index + ".jpg");
                    if (!resource.exists()) {
                        return null;
                    }
                    byte[] bytes = resource.getInputStream().readAllBytes();
                    String base64Image = Base64.getEncoder().encodeToString(bytes);
                    ItemGroup itemGroup = getGroup(index / 6);
                    Image64 image = new Image64("data:image/jpeg;base64," + base64Image, index, itemGroup);
                    return gson.toJson(image) + "\n";
                })
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(e -> {
                    System.err.println("Error loading image " + index + ": " + e.getMessage());
                    return Mono.empty();
                });
    }


    private ItemGroup getGroup(int index) {
        Group superGroup = index > 5 ? Group.DRINKS : Group.FOOD;
        ItemGroup itemGroup = new ItemGroup(index, groups.get(index), new LinkedList<>(), superGroup);
        for (int i = index * 6; i < index * 6 + 6; i++) {
            itemGroup.addItem(i);
        }

        return itemGroup;
    }


    @GetMapping("/menu")
    public ResponseEntity<List<ItemGroup>> getMenu() {
        List<ItemGroup> groups = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            groups.add(getGroup(i));
        }
        return ResponseEntity.ok().body(groups);
    }
}
