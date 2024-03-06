package com.example.webclientservice.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.Scanner;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;


public class EmployeeClient {

    private final WebClient webClient;
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        EmployeeClient employeeClient = new EmployeeClient(WebClient.create());
        employeeClient.presentMenu(employeeClient);
        System.out.println("Good bye, thanks for playing.");
    }

    public EmployeeClient(WebClient webClient) {
        this.webClient = webClient;
    }

    private void presentMenu(EmployeeClient employeeClient) {
        Scanner scanner = new Scanner(System.in);

        boolean exit = false;
        do {

            System.out.println("\nWelcome to the Animal App!");
            System.out.println("1. List all employees (not yet implemented)");
            System.out.println("2. Find an employee (not yet implemented)");
            System.out.println("3. Add an employee (not yet implemented)");
            System.out.println("4. Change an employee (not yet implemented)");
            System.out.println("5. Delete an employee (not yet implemented)");
            System.out.println("99. Exit");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("The list of employees is:" + employeeClient.listEmployees());
                    break;
                case 99:
                    System.out.println("Exit selected.");
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice: " + choice);
                    break;
            }
        } while (!exit);
    }

    private String listEmployees() {
        String url = "https://dummy.restapiexample.com/api/v1/employees";

        try {
            String responseBody = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Process successful response here
            System.out.println("Successfully retrieved data:");
            return prettifyMyJson(responseBody);

        } catch (WebClientResponseException e) {
            if (HttpStatus.TOO_MANY_REQUESTS.equals(e.getStatusCode())) {
                System.out.println("Web server says too many requests. Please try again later.");
            } else {
                System.out.println("An unexpected client error occurred: " + e.getMessage());
            }
        }
        return null;
    }

    private static String prettifyMyJson(String result) {
        String prettifiedJson = "";
        try {
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            prettifiedJson = mapper.writeValueAsString(mapper.readValue(result, Object.class));
        } catch(Exception ex) {
            System.out.println("Error while pretty printing JSON: " + ex.getMessage());
        }
        return prettifiedJson;
    }
}
