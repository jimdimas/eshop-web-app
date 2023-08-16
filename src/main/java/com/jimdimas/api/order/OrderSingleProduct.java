package com.jimdimas.api.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jimdimas.api.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name="OrderSingleProduct",
        uniqueConstraints = @UniqueConstraint(columnNames = {"order_id","product_id"})
)   //this unique constraint means you can't have an order with the same product referred more than once
//This class exists as a table , it holds information about which products an order has and their quantity
public class OrderSingleProduct {
    @Id
    @SequenceGenerator(
            name="order_single",
            sequenceName = "order_single",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "order_single",
            strategy = GenerationType.SEQUENCE
    )
    @JsonIgnore
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id",referencedColumnName = "orderId",nullable = false)
    @JsonBackReference  //annotation used to stop circular reference to order
    private Order order;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="product_id",referencedColumnName = "productId")
    private Product product;
    private Integer quantity;

}
