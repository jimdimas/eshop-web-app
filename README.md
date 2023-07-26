# E-shop web application

Hello and welcome to my sample web application repo for an eshop.
I will develop the backend using Spring Boot and frontend using React.js .

## Getting started

In order to get the backend of the project started , you need to go to https://start.spring.io and initialize
a Maven project.The dependencies you are going to need are:
* Spring Web
* Spring Data JPA
* Lombok
* PostgreSQL Driver , the database i'm going to use

After downloading the project , open it in any IDE suited for Java development.
I am personally using IntelliJ.

Lombok is an added dependency , which helps us reducing boilerplate code , such as getters/setters , required constructors etc.

We will also add Spring Security later on , but i will keep i will not include for now in order to test the application.

## Setting up application.properties file

You need to set up this file beforehand , in order to be able to connect with the database.
The format of the file should be the following:

_**spring.datasource.url=jdbc:postgresql://localhost:5432/{dbname}
spring.datasource.username={username} 
spring.datasource.password={password}
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.properties.hibernate.format_sql=true**_

The port specified in the datasource might be different , but the standard one used
for PostgreSQL is 5432.
Every value with brackets means that it depends on the configurations of your db.
Also , the ddl-auto value create-drop , might be changed later on but we will keep it like
this for now for testing purposes.

