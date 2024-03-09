package com.example.webclientservice.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.Scanner;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;


public class EmployeeClient {

    public static final String TOO_MANY_REQUESTS_MESSAGE = "Web server says too many requests. Please try again later.";
    public static final String CLIENT_ERROR_OCCURRED = "An unexpected client error occurred: ";
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

            System.out.println("\nWelcome to the Employee App!");
            System.out.println("1. List all employees");
            System.out.println("2. Find an employee");
            System.out.println("3. Add an employee ");
            System.out.println("4. Change an employee");
            System.out.println("5. Delete an employee (not yet implemented)");
            System.out.println("99. Exit");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("The list of employees is:" + employeeClient.listEmployees());
                    break;
                case 2:
                    System.out.println("A single employee:" + employeeClient.findEmployeeById());
                    break; 
                case 3:
                    System.out.println("Create Employee:" + employeeClient.createEmployee());
                    break;
                case 4:
                    System.out.println("Create Employee:" + employeeClient.updateEmployee(this));
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
            System.out.println("Successfully retrieved data:"); // TODO make into log statement
            return prettifyMyJson(responseBody);

        } catch (WebClientResponseException e) {
            if (HttpStatus.TOO_MANY_REQUESTS.equals(e.getStatusCode())) {
                System.out.println(TOO_MANY_REQUESTS_MESSAGE);
            } else {
                System.out.println(CLIENT_ERROR_OCCURRED + e.getMessage());
            }
        }
        return null;
    }

    private String findEmployeeById() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the id of the employee you want to find:  ");
        long id = scanner.nextLong();
        String url = "https://dummy.restapiexample.com/api/v1/employee/" + id;
        System.out.println("You want employee with id:  " + id);

        try {
            String responseBody = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("Successfully retrieved data:");
            return prettifyMyJson(responseBody);

        } catch (WebClientResponseException e) {
            if (HttpStatus.TOO_MANY_REQUESTS.equals(e.getStatusCode())) {
                System.out.println(TOO_MANY_REQUESTS_MESSAGE);
            } else {
                System.out.println(CLIENT_ERROR_OCCURRED + e.getMessage());
            }
        }
        return null;
    }



       private String createEmployee() {

        String url = "https://dummy.restapiexample.com/api/v1/create";

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the name :  ");
        String name = scanner.nextLine();
        System.out.println("Enter the salary per year :  ");
        String salary = scanner.nextLine();
        System.out.println("Enter the age :  ");
        String age = scanner.nextLine();
        String newEmployee = "You want to create employee with Name:  " + name + "  Annual Salary:  " + salary + "  Age:  " + age;
        System.out.println(newEmployee);

        try {
            String body = "{\"name\":\"" + name + "\",\"salary\":\"" + salary + "\",\"age\":\"" + age + "\"}";
            String responseBody = webClient.post()
                .uri(url)
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .bodyToMono(String.class)
                .block();

            System.out.println("Successfully added employee: " + newEmployee); // TODO make into log statement
            return prettifyMyJson(responseBody);

        } catch (WebClientResponseException e) {
            if (HttpStatus.TOO_MANY_REQUESTS.equals(e.getStatusCode())) {
                System.out.println(TOO_MANY_REQUESTS_MESSAGE);
            } else {
                System.out.println(CLIENT_ERROR_OCCURRED + e.getMessage());
            }
        }
        return null;
    }

        private String updateEmployee(EmployeeClient employeeClient) {

        System.out.println("Next enter id of employee to update:  ");

        String baseUrlPut = "https://dummy.restapiexample.com/api/v1/update/";
            System.out.println("break here");
        String foundEmployee = employeeClient.findEmployeeById();

            Mono<String> jsonStringMono = Mono.just(foundEmployee);

            jsonStringMono.subscribe(jsonString -> {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = null;
                try {
                    rootNode = mapper.readValue(jsonString, JsonNode.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                // Access "data" node
                JsonNode dataNode = rootNode.path("data");

                // Extract desired values
                String name = dataNode.path("employee_name").asText();
                int age = dataNode.path("employee_age").asInt();
                int salary = dataNode.path("employee_salary").asInt();

                System.out.println("Employee Name: " + name);
                System.out.println("Employee Age: " + age);
                System.out.println("Employee Salary: " + salary);
            });

        System.out.println("break here");

            System.out.println(foundEmployee);
            return null;




//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Enter the name :  ");
//        String name = scanner.nextLine();
//        System.out.println("Enter the salary per year :  ");
//        String salary = scanner.nextLine();
//        System.out.println("Enter the age :  ");
//        String age = scanner.nextLine();
//        String newEmployee = "You want to create employee with Name:  " + name + "  Annual Salary:  " + salary + "  Age:  " + age;
//        System.out.println(newEmployee);
//
//        try {
//            String body = "{\"name\":\"" + name + "\",\"salary\":\"" + salary + "\",\"age\":\"" + age + "\"}";
//            String responseBody = webClient.post()
//                .uri(baseUrlPut)
//                .body(BodyInserters.fromValue(body))
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
//
//            System.out.println("Successfully added employee: " + newEmployee); // TODO make into log statement
//            return prettifyMyJson(responseBody);
//
//        } catch (WebClientResponseException e) {
//            if (HttpStatus.TOO_MANY_REQUESTS.equals(e.getStatusCode())) {
//                System.out.println(TOO_MANY_REQUESTS_MESSAGE);
//            } else {
//                System.out.println(CLIENT_ERROR_OCCURRED + e.getMessage());
//            }
//        }
//        return null;
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
