package com.example.pizzasender.applicationController;

// PizzaController.java

// PizzaController.java

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Controller
public class PizzaSenderController {

    private static final String RECEIVER_URL = "http://localhost:8081/pizza-receiver";

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/send-pizza")
    public ResponseEntity<String> sendPizza() {
        // Create the Pizza object for Margarita pizza
        System.out.println("Sending pizza data:");
        Pizza margaritaPizza = new Pizza();
        margaritaPizza.setName("Margarita");
        margaritaPizza.setIngredients(Arrays.asList("sauce", "mozzarella"));
        margaritaPizza.setPrice(25.0);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // Log the data being sent to the console
        System.out.println("Sending pizza data:");
        System.out.println(margaritaPizza);

        HttpEntity<Pizza> requestEntity = new HttpEntity<>(margaritaPizza, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                RECEIVER_URL,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        return ResponseEntity.ok(responseEntity.getBody());
    }
}



