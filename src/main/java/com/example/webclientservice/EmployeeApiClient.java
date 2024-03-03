package com.example.webclientservice;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

public class EmployeeApiClient {

    public static void main(String[] args) {
        final String url = "https://dummy.restapiexample.com/api/v1/employees";

        // Create a WebClient instance
        WebClient webClient = WebClient.create();

        // Send the GET request and handle the response
        webClient.get()
                .uri(URI.create(url))
                .retrieve()
                .bodyToMono(String.class) // Convert response body to String
                .subscribe(response -> {
                    System.out.println("Employee data:");
                    System.out.println(response); // Print the employee data
                });

        System.out.println("stuff" + webClient);

        WebClient client = WebClient.create();

        WebClient.ResponseSpec responseSpec = client.get()
                .uri(url)
                .retrieve();

        var thing = responseSpec;

        System.out.println("responseSpec is: " + responseSpec);




    }





}
