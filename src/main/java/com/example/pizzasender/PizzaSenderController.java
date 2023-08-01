package com.example.pizzasender;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
@RequestMapping("/pizza-sender")
public class PizzaSenderController {

    @PostMapping("/send-pizza")
    @ResponseBody
    public ResponseEntity<String> sendPizzaToReceiver() {
        // Create the Peperoni pizza object
        Pizza peperoniPizza = new Pizza();
        peperoniPizza.setName("Peperoni");
        peperoniPizza.setIngredients(List.of("mozarella", "sauce", "peperoni"));
        peperoniPizza.setPrice(25.0);

        // Convert pizza object to JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Pizza> requestEntity = new HttpEntity<>(peperoniPizza, headers);

        // Send the pizza to pizza-receiver using POST
        RestTemplate restTemplate = new RestTemplate();
        String receiverUrl = "http://localhost:8081/pizza-receiver";
        ResponseEntity<String> response = restTemplate.postForEntity(receiverUrl, requestEntity, String.class);

        // You can handle the response from pizza-receiver here if needed
        return response;
    }
}


