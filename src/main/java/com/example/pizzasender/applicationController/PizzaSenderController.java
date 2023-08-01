package com.example.pizzasender.applicationController;

// PizzaController.java

// PizzaController.java

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PizzaSenderController {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Method to convert each Pizza to a JSON object
    private static String convertPizzaToJson(Pizza pizza) throws JsonProcessingException {
        return objectMapper.writeValueAsString(pizza);
    }

    // Method to send the JSON string to the receiver app
    private static void sendPizzaToReceiver(String pizzaJson) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/pizza-receiver"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(pizzaJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Response status code: " + response.statusCode());
        System.out.println("Response body: " + response.body());
    }

    // Create and send the list of pizzas to the receiver app
    private static void createAndSendPizzas() throws JsonProcessingException, IOException, InterruptedException {
        List<Pizza> pizzas = new ArrayList<>();
        pizzas.add(new Pizza("Margarita", Arrays.asList("sauce", "mozzarella"), 25));
        pizzas.add(new Pizza("Pepperoni", Arrays.asList("sauce", "mozzarella", "pepperoni"), 30));
        pizzas.add(new Pizza("Hawaii", Arrays.asList("sauce", "mozzarella", "ham", "pineapple"), 40));

        for (Pizza pizza : pizzas) {
            String pizzaJson = convertPizzaToJson(pizza);
            System.out.println(pizzaJson);
            sendPizzaToReceiver(pizzaJson);
        }
    }

    public static void main(String[] args) throws JsonProcessingException, IOException, InterruptedException {
        createAndSendPizzas();

    }
}






