package com.example.pizzasender.applicationController;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@RestController
public class PizzaSenderController {
    private List<Pizza> createPizzas() {
        List<Pizza> pizzas = new ArrayList<>();
        pizzas.add(new Pizza("Margarita", Arrays.asList("sauce", "mozzarella"), 25));
        pizzas.add(new Pizza("Pepperoni", Arrays.asList("sauce", "mozzarella", "pepperoni"), 30));
        pizzas.add(new Pizza("Hawaii", Arrays.asList("sauce", "mozzarella", "ham", "pineapple"), 40));
        return pizzas;
    }

    private final String receiverUrl = "http://localhost:8081/pizza-receiver";

    // Remove @Autowired since RestTemplate is provided by RestTemplateConfig
    private final RestTemplate restTemplate;

    public PizzaSenderController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/send-pizzas")
    public ResponseEntity<String> sendPizzas() {
        List<Pizza> pizzas = createPizzas();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Schedule sending each pizza separately with a delay of 250 milliseconds
        for (int i = 0; i < 20; i++) {
            Pizza randomPizza = pizzas.get(new Random().nextInt(pizzas.size()));

            scheduler.schedule(() -> {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<String> requestEntity = new HttpEntity<>(randomPizzaToJsonString(randomPizza), headers);
                restTemplate.postForEntity(receiverUrl, requestEntity, Void.class);
            }, i * 250, TimeUnit.MILLISECONDS);
        }

        // Stop sending after 5 seconds (if needed)
        // scheduler.schedule(() -> scheduler.shutdown(), 5, TimeUnit.SECONDS);

        return ResponseEntity.ok("Sending pizzas in progress!");
    }

    private String randomPizzaToJsonString(Pizza pizza) {
        // Use a library like Jackson to convert the Pizza object to JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(pizza);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}"; // Return an empty JSON object as a fallback
        }
    }
}








