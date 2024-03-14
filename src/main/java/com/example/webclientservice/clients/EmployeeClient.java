package com.example.webclientservice.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;


public class EmployeeClient {

    public static final String TOO_MANY_REQUESTS_MESSAGE = "Web server says too many requests. Please try again later. as of " + LocalTime.now();
    public static final String CLIENT_ERROR_OCCURRED = "An unexpected client error occurred: ";
    private final WebClient webClient;
    private static final ObjectMapper mapper = new ObjectMapper();
    String breakpoint = null;

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

            System.out.println("\nWelcome to the Employee App!  Note the server only allows 1 request per minute.  Please wait 1 minute between requests.  Thank you.  Current time is:  " + LocalDate.now() + "  " + LocalTime.now());
            System.out.println("1. List all employees");
            System.out.println("2. Find an employee");
            System.out.println("3. Add an employee ");
            System.out.println("4. Change an employee");
            System.out.println("5. Delete an employee");
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
                case 5:
                    System.out.println("Delete Employee:" + employeeClient.deleteEmployee());
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
                System.out.println(TOO_MANY_REQUESTS_MESSAGE + " attempting list Employees");
            } else {
                System.out.println(CLIENT_ERROR_OCCURRED + e.getMessage());
            }
        }
        return null;
    }

    private String findEmployeeById() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the id of the employee:  ");
        long id = scanner.nextLong();
        String url = "https://dummy.restapiexample.com/api/v1/employee/" + id;
        System.out.println("For employee with id of:  " + id);

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
                System.out.println(TOO_MANY_REQUESTS_MESSAGE + " attempting findByEmployee");
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

        Map<String, Object> stringToObject = new HashMap<>();
        stringToObject.put("name", name);
        stringToObject.put("salary", salary);
        stringToObject.put("age", age);

        String body;
        try {
            body = mapper.writeValueAsString(stringToObject);
        } catch (JsonProcessingException e) {
            // Handle JSON processing exception if occurs
            e.printStackTrace();
            // You can throw a custom exception or handle it differently
            throw new RuntimeException("Error creating JSON body");
        }


        try {
            String responseBody = webClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON) // Set Content-Type header
                    .body(BodyInserters.fromValue(body))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("Successfully added employee: " + newEmployee); // TODO make into log statement
            return prettifyMyJson(responseBody);

        } catch (WebClientResponseException e) {
            if (HttpStatus.TOO_MANY_REQUESTS.equals(e.getStatusCode())) {
                System.out.println(TOO_MANY_REQUESTS_MESSAGE + "attempting createEmployee");
            } else {
                System.out.println(CLIENT_ERROR_OCCURRED + e.getMessage());
            }
        }
        return null;
    }

    private String updateEmployee(EmployeeClient employeeClient) {

        String baseUrlPut = "https://dummy.restapiexample.com/api/v1/update/";
        String foundEmployee = null;

        try {
            foundEmployee = employeeClient.findEmployeeById();
        } catch (Exception e) {
            System.out.println("Exception in updateEmployee findEmployeeById"); // TODO make into log statement
            throw new RuntimeException(e);
        }

        // We have the employee JSON object, now to extract the fields
        final String[] name = {""};
        final String[] age = {""};
        final String[] salary = {""};
        final String[] id = {""};

        Mono<String> jsonStringMono = null;
        try {
            jsonStringMono = Mono.just(foundEmployee);

            jsonStringMono.subscribe(jsonString -> {
                JsonNode rootNode = null;
                try {
                    rootNode = mapper.readValue(jsonString, JsonNode.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                // Access "data" node
                JsonNode dataNode = rootNode.path("data");

                // Extract desired values
                name[0] = dataNode.path("employee_name").asText();
                age[0] = dataNode.path("employee_age").asText();
                salary[0] = dataNode.path("employee_salary").asText();
                id[0] = dataNode.path("id").asText();

                System.out.println("Employee Name: " + name[0]);
                System.out.println("Employee Age: " + age[0]);
                System.out.println("Employee Salary: " + salary[0]);
            });

        } catch (Exception e) {
            System.out.println("Exception in extracting fields from found employee"); // TODO make into log statement
            return null;
            //throw new RuntimeException(e);  //TODO:  Think about the best way to handle this exception may be related to the 1 minute limit
        }

        // Now we have the fields, let's update them...first checking to see which user wants to change
        String valueOfName = name[0];
        String valueOfAge = age[0];
        String valueOfSalary = salary[0];

        Scanner scanner = new Scanner(System.in);

        System.out.println("Current name is " + valueOfName + " enter new name or press enter to keep old name");
        String nameInput = scanner.nextLine();
        valueOfName = !nameInput.isEmpty() ? nameInput : valueOfName;
        System.out.println("so now the name will be " + valueOfName);

        System.out.println("Current age is " + valueOfAge + " enter new age or press enter to keep old age");
        String ageInput = scanner.nextLine();
        valueOfAge = !nameInput.isEmpty() ? ageInput : valueOfAge;
        System.out.println("The age will be " + valueOfAge);

        System.out.println("Current age is " + valueOfSalary + " enter new salary or press enter to keep old salary");
        String salaryInput = scanner.nextLine();
        valueOfSalary = !nameInput.isEmpty() ? salaryInput : valueOfSalary;
        System.out.println("The salary will be " + valueOfSalary);

        Map<String, Object> stringToObject = new HashMap<>();
        stringToObject.put("name", valueOfName);
        stringToObject.put("salary", valueOfSalary);
        stringToObject.put("age", valueOfAge);

        String body;
        try {
            body = mapper.writeValueAsString(stringToObject);
        } catch (JsonProcessingException e) {
            // Handle JSON processing exception if occurs
            e.printStackTrace();
            // You can throw a custom exception or handle it differently
            throw new RuntimeException("Error creating JSON body");
        }
        // wait 1 minute for server to allow new request
        try {
            System.out.println("waiting 1 minute for server to allow new request");
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String url = baseUrlPut + id[0];
        System.out.println("url is:  " + url);
        try {
            String responseBody = webClient.put()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON) // Set Content-Type header
                    .body(BodyInserters.fromValue(body))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("Successfully updated employee with id of " + id[0] + " and fields of:  " + body); // TODO make into log statement
            System.out.println("Successfully updated employee: " + responseBody);
            return prettifyMyJson(responseBody);
        } catch (WebClientResponseException e) {
            if (HttpStatus.TOO_MANY_REQUESTS.equals(e.getStatusCode())) {
                System.out.println(TOO_MANY_REQUESTS_MESSAGE + " attempting updateEmployee");
            } else {
                System.out.println(CLIENT_ERROR_OCCURRED + e.getMessage());
            }
        }
        return null;  //TODO investigate if this is the prooper return
    }
    private String deleteEmployee() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the id of the employee:  ");
        long id = scanner.nextLong();
        String url = "https://dummy.restapiexample.com/api/v1/delete/" + id;
        System.out.println("For employee with id of:  " + id);

        try {
            String responseBody = webClient.delete()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("Successfully deleted data:"); //TODO make into log statement
            return prettifyMyJson(responseBody);

        } catch (WebClientResponseException e) {
            if (HttpStatus.TOO_MANY_REQUESTS.equals(e.getStatusCode())) {
                System.out.println(TOO_MANY_REQUESTS_MESSAGE + " attempting findByEmployee");
            } else {
                System.out.println(CLIENT_ERROR_OCCURRED + e.getMessage());
            }
        }
        return null;
    }

    private static String prettifyMyJson(String result) {
        String prettifiedJson = "";
        try {
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            prettifiedJson = mapper.writeValueAsString(mapper.readValue(result, Object.class));
        } catch (Exception ex) {
            System.out.println("Error while pretty printing JSON: " + ex.getMessage());
        }
        return prettifiedJson;
    }
}
