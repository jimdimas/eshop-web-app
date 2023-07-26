package com.jimdimas.api.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "_user")  //user is a keyword and reserved in postgresql
@Data                   //lombok keyword that generates getters/setters automatically
public class User {
    @Id
    @SequenceGenerator(
            name="user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1)
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence")
    @JsonIgnore //Ignoring all
    private Integer id;
    private String firstName;
    private String lastName;
    @JsonIgnore
    private String password;
    @Column(unique = true)
    private String email;
    private LocalDate dob;
    @Enumerated(EnumType.STRING)
    @JsonIgnore
    private Role role;
}
