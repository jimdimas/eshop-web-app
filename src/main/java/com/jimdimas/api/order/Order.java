package com.jimdimas.api.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jimdimas.api.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="_order",indexes = {
        @Index(name="order_id_idx",columnList = "orderId")
})   //order is a keyword in postgresql
public class Order {

    @Id
    @SequenceGenerator(
            name="order_sequence",
            sequenceName = "order_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "order_sequence",
            strategy = GenerationType.SEQUENCE
    )
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Integer id;
    @Column(unique = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID orderId;
    @ManyToOne
    @JoinColumn(name="username",referencedColumnName = "username")
    @JsonIgnoreProperties({"dob"})
    private User user;
    private LocalDateTime orderTime;
    private OrderState orderState;
    @OneToMany(mappedBy = "order",fetch = FetchType.EAGER,cascade = CascadeType.REMOVE)
    @JsonManagedReference   //annotation used to stop circular reference to OrderSingleProduct
    private List<OrderSingleProduct> cartProducts;
    @Getter(AccessLevel.NONE)
    private Integer totalPrice;
    @JsonIgnore
    private String verificationToken;

    public Integer getTotalPrice(){
        Integer sum=0;
        for (OrderSingleProduct item: cartProducts){
            sum+=item.getProduct().getPrice()* item.getQuantity();
        }
        return sum;
    }
}
