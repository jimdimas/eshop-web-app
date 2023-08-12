package com.jimdimas.api.review;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jimdimas.api.product.Product;
import com.jimdimas.api.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Review {

    @Id
    @SequenceGenerator(
            name = "review_sequence",
            sequenceName = "review_sequence",
            allocationSize = 1)
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "review_sequence")
    @JsonIgnore
    private Integer id;
    @Column(unique = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID reviewId;
    @ManyToOne
    @JoinColumn(name = "user_review",referencedColumnName = "username")
    private User user;

    @ManyToOne
    @JoinColumn(name="product_review",referencedColumnName = "productId")
    private Product product;

    private Integer rating;
    private String text;
}
