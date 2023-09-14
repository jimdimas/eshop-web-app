package com.jimdimas.api.token;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jimdimas.api.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token {

    @Id
    @SequenceGenerator(
            name = "token_sequence",
            sequenceName = "token_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "token_sequence"
    )
    @JsonIgnore
    private Integer id;
    @Column(unique = true)
    private String refreshToken;    //hashed JWT refresh token,actual JWT refresh tokens not stored in database
    @OneToOne
    @JoinColumn(name="username",referencedColumnName = "username")
    private User user;
    private Date expiresAt;
}
