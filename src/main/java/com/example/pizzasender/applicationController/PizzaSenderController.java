package com.example.pizzasender.applicationController;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping("/pizza")
public class PizzaSenderController {
    private List<Pizza> createPizzas() {
        List<Pizza> pizzas = new ArrayList<>();
        pizzas.add(new Pizza("Margarita", Arrays.asList("sauce", "mozzarella"), 25));
        pizzas.add(new Pizza("Pepperoni", Arrays.asList("sauce", "mozzarella", "pepperoni"), 30));
        pizzas.add(new Pizza("Hawaii", Arrays.asList("sauce", "mozzarella", "ham", "pineapple"), 40));
        // Add more pizza variations if needed
        return pizzas;
    }

    private final String receiverUrl = "http://localhost:8081/pizza-receiver";

    @Autowired
    private RestTemplate restTemplate;

    @Value("${pizzas.per.second.default:10}")
    private int pizzasPerSecondDefault;

    @Value("${sending.duration.seconds.default:10}")
    private int sendingDurationSecondsDefault;

    @GetMapping("/send")
    public ResponseEntity<String> sendPizzas(
            @RequestParam(value = "pizzasPerSecond", required = false) Integer pizzasPerSecond,
            @RequestParam(value = "sendingDurationSeconds", required = false) Integer sendingDurationSeconds
    ) {
        int pizzasPerSec = pizzasPerSecond != null ? pizzasPerSecond : pizzasPerSecondDefault;
        int sendingDurationSec = sendingDurationSeconds != null ? sendingDurationSeconds : sendingDurationSecondsDefault;

        List<Pizza> pizzas = createPizzas();
        int totalPizzasToSend = pizzasPerSec * sendingDurationSec;

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Schedule sending pizzas with the desired rate
        for (int i = 0; i < totalPizzasToSend; i++) {
            Pizza randomPizza = pizzas.get(new Random().nextInt(pizzas.size()));

            scheduler.schedule(() -> {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<String> requestEntity = new HttpEntity<>(randomPizzaToJsonString(randomPizza), headers);
                restTemplate.postForEntity(receiverUrl, requestEntity, Void.class);
            }, i * 1000 / pizzasPerSec, TimeUnit.MILLISECONDS);
        }

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

    // ... (other methods if needed)
}







