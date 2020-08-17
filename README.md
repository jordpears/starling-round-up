# Starling-Bank Roundup Microservice
A spring-boot application to call Starling Bank APIs to complete the roundup feature. 

Start the service with `mvn spring-boot:run`

The endpoint is http://localhost:8080/weekly-roundup

Once the service is running you can view the generated documentation and call the service using the following swagger URL http://localhost:8080/swagger-ui.html

For validation the service needs a header as documented in the above swagger - this token should be the exact string as taken from a sandbox customers Access Token. I am using this token to auth the downstream API calls.

[header](header.png)