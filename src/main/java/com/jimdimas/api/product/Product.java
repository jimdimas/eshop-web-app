package com.jimdimas.api.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jimdimas.api.review.Review;
import com.jimdimas.api.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="product",indexes = {
        @Index(name="product_id_idx",columnList = "productId")
})
public class Product {

    @Id
    @SequenceGenerator(
            name="product_sequence",
            sequenceName = "product_sequence",
            allocationSize = 1)
    @GeneratedValue(
            generator = "product_sequence",
            strategy=GenerationType.SEQUENCE
            )
    @JsonIgnore
    private Integer id;

    @Column(unique = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID productId; //We keep this as the public visible id and keep the other id for db primary key hidden

    @ManyToOne
    @JoinColumn(name="user_id",referencedColumnName = "id")
    private User user;
    private String name;
    private String description;
    private Category category;
    private Integer price;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)   //creation date is set by the server
    private LocalDate creationDate;
    private Integer quantity;
    @OneToMany(mappedBy = "product")
    @JsonManagedReference
    private List<Review> reviews;
    @Getter(AccessLevel.NONE)
    private Double rating;

    public Double getRating() {
        Double sum=0.0;
        for (Review review:this.reviews){
            sum+=review.getRating();
        }
        return sum/ reviews.size();
    }
}
