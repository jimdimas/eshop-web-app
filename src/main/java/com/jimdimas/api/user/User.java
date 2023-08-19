package com.jimdimas.api.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "_user",indexes = {
        @Index(name="user_email_idx",columnList = "email"),
        @Index(name="username_idx",columnList = "username")
})  //user is a keyword and reserved in postgresql
@Data                   //lombok keyword that generates getters/setters automatically
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"enabled","authorities","accountNonExpired","credentialsNonExpired","accountNonLocked"}) //ignoring inherited fields from UserDetails
public class User implements UserDetails {
    @Id
    @SequenceGenerator(
            name="user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1)
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence")
    @JsonIgnore //Ignoring fields returned for security purposes
    private Integer id;
    private String firstName;
    private String lastName;
    @JsonProperty(access= JsonProperty.Access.WRITE_ONLY)   //field is accessible to write , i.e. post a user but cannot be viewed in a get
    private String password;
    @JsonIgnore
    private String verificationToken;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)  //can provide a password token in i.e. a post request, but cannot view it
    private String passwordToken;
    @JsonIgnore
    private LocalDateTime passwordTokenExpirationDate;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String username;
    private LocalDate dob;
    @Enumerated(EnumType.STRING)
    @JsonIgnore
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override   //lombok already created this method,but override it to implement all UserDetails methods
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        if (this.verificationToken.isEmpty()){
            return true;
        }
        return false;
    }
}
