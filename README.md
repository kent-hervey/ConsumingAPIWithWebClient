WebClient Demo project

Overall: The purpose of this project is to demonstrate how to use the WebClient to consume a REST API.  Thus, you will see techniques and code you would not see in production code, or even demonstration code with a wider purpose.



* Source and references
    * https://dummyapis.com/
    * For Employee:  https://dummy.restapiexample.com/
    * For NumbersFacts:  https://rapidapi.com/divad12/api/numbers-1/
    * https://blog.stoplight.io/api-keys-best-practices-to-authenticate-apis

**Employee Demo**

  * Notes
    * Source API allows full crud, but unfortunately only allows 1 request per minute

    * Equivalent CURL commands
      * Fetch all employees
        * curl --location 'https://dummy.restapiexample.com/api/v1/employees'
      * Fetch one employee
        * curl --location 'https://dummy.restapiexample.com/api/v1/employee/1' | jq
      * Create an employee
        * curl --location --request POST 'https://dummy.restapiexample.com/api/v1/create' --header 'Content-Type: application/json' --data '{"name":"Kim Jones","salary":"999999","age":"16"}' | jq
      * Update an employee
        * curl --location --request PUT 'https://dummy.restapiexample.com/api/v1/update/21' --header 'Content-Type: application/json' --data '{"name":"Jenette Caldwell","salary":"345000","age":"30"}'   | jq
      * Delete an employee
        * curl --location --request DELETE 'https://dummy.restapiexample.com/public/api/v1/delete/2' | jq

  * TODOs
    * [ ] Implement logger in various places that might currently use sout
    * [ ] Add a description of the project
    * For Employees
      * [X] Add a method to get all employees
      * [X] Add a method to get an employee by id
      * [X] Add a method to create an employee
      * [X] Add a method to update an employee
      * [ ] Add a method to delete an employee
    * [] NumbersFacts https://rapidapi.com/divad12/api/numbers-1/