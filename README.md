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

You should add Spring Security when you start implementing the authentication/authorization scheme of the application,in order
to be able to do basic tests before.You can add it manually later by including the following in pom.xml file:

    <dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
			<groupId>org.springframework.security</groupId>
			<artifactId>spring-security-test</artifactId>
			<scope>test</scope>
    </dependency>

After you add these lines to pom.xml and save them you need to right click on the file and click on Maven>Reload project 
in order for the new dependencies to be included.

The application is going to use JWT for auth.Check https://jwt.io for more info.In order to include the dependencies for
JWT you need to add to the pom.xml file the following:

    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.11.5</version>
    </dependency>

Again , you need to save the file and right click>Maven>Reload project to include the changes.

## Setting up application.properties file

You need to set up this file beforehand.
The format of the file should be the following:

_**spring.datasource.url=jdbc:postgresql://localhost:5432/{dbname}
spring.datasource.username={username} 
spring.datasource.password={password}
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.properties.hibernate.format_sql=true
spring.properties.jwt.secret_key={your_secret_key}**_

The port specified in the datasource might be different , but the standard one used
for PostgreSQL is 5432.
Every value with brackets means that it depends on the configurations of your db.
Also , the ddl-auto value create-drop , might be changed later on but we will keep it like
this for now for testing purposes.
The secret key should be generated either by an online tool or by a script.Don't put a manual key.
The implementation I did uses a 256-bit key architecture.

