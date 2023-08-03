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

The application is going to use JWT for auth.Check https://jwt.io for more info.
We are going to use the auth0 RS256 implementation of JWT.
In order to include the dependencies for JWT you need to add to the pom.xml file the following:

    <dependency>
			<groupId>com.auth0</groupId>
			<artifactId>java-jwt</artifactId>
			<version>4.4.0</version>
    </dependency>

Again , you need to save the file and right click>Maven>Reload project to include the changes.

## Setting up application.yml file

You need to set up this file beforehand.This file must remain hidden from the endpoint users and from any versioning software
or uploads , because it contains sensitive application data such as username/password for DB , RSA keys etc.
That's why i keep this file included in .gitignore and you need to set it up on your own with your custom values.
The format of the file should be the following:

_** spring:
        datasource:
            password: {db_password}
            url: jdbc:postgresql://localhost:5432/{db_name}
            username: {db_username}
        jpa:
            hibernate:
                ddl-auto: create-drop
            properties:
                hibernate:
                    dialect: org.hibernate.dialect.PostgreSQLDialect
            show-sql: true
        properties:
            hibernate:
                format_sql: true
            jwt: 
                private_key: |
                -----BEGIN PRIVATE KEY-----
                {your generated private key here}
                -----END PRIVATE KEY-----
                public_key:  |
                -----BEGIN PUBLIC KEY-----
                {your generated public key here}
                -----END PUBLIC KEY-----
**_

The port specified in the datasource might be different , but the standard one used
for PostgreSQL is 5432.
Every value with brackets means that it depends on your custom configurations.
Also , the ddl-auto value create-drop , might be changed later on but we will keep it like
this for now for testing purposes.
The key pair for RS256 JWT can be generated by the following script:

    ssh-keygen -t rsa -b 4096 -m PKCS8 -f jwtRS256.key
    # Don't add passphrase
    openssl rsa -in jwtRS256.key -pubout -outform PEM -out jwtRS256.key.pub

Open the files afterwards and copy the contents , not the headers, of the keys into the corresponding values
of private_key and public_key.

