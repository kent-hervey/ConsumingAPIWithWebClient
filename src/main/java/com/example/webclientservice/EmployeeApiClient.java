package com.example.webclientservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;


public class EmployeeApiClient {

    public static void main(String[] args) {
        final String url = "https://dummy.restapiexample.com/api/v1/employees";

        // Create a WebClient instance
        WebClient webClient = WebClient.create();

        try {
            String responseBody = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Process successful response here
            System.out.println("Successfully retrieved data:");
            System.out.println(prettifyMyJson(responseBody));

        } catch (WebClientResponseException e) {
            if (e.getStatusCode().is4xxClientError() && e.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)) {
                System.out.println("Web server says too many requests. Please try again later.");
            } else {
                // Handle other client errors
                System.out.println("An unexpected client error occurred: " + e.getMessage());
            }
        }
    }

    private static String prettifyMyJson(String result) {
        String prettifiedJson = "";
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            prettifiedJson = mapper.writeValueAsString(mapper.readValue(result, Object.class));
        } catch(Exception ex) {
            System.out.println("Error while pretty printing JSON: " + ex.getMessage());
        }
        return prettifiedJson;
    }

}
