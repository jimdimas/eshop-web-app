package com.jimdimas.api.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order,Integer> {

    @Query("SELECT o FROM Order o WHERE o.user.username=?1")
    List<Order> findByUsername(String username);

    @Query("SELECT o FROM Order o WHERE o.orderId=?1")
    Optional<Order> findByPublicId(UUID orderId);
}
