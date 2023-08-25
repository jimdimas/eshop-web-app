package com.jimdimas.api.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderSingleProductRepository extends JpaRepository<OrderSingleProduct,Integer> {
    @Query("SELECT o FROM OrderSingleProduct o WHERE o.order.orderId=:orderId AND o.product.productId=:productId")
    Optional<OrderSingleProduct> findProductInOrder(@Param("orderId") UUID orderId,@Param("productId") UUID productId);
}
