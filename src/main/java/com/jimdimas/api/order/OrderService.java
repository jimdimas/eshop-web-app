package com.jimdimas.api.order;

import com.jimdimas.api.product.Product;
import com.jimdimas.api.product.ProductService;
import com.jimdimas.api.user.Role;
import com.jimdimas.api.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderSingleProductRepository orderSingleProductRepository;
    public List<Order> getAllOrders(User user) {
        if (user.getRole().equals(Role.ADMIN)){
            return orderRepository.findAll();
        }
        else{
            return orderRepository.findByUsername(user.getUsername());
        }
    }

    public void addOrder(User user, List<RequestSingleProduct> requestSingleProducts) {
        List<OrderSingleProduct> finalOrderProducts = new ArrayList<>();
        Order order = Order.builder()
                .orderId(UUID.randomUUID())
                .user(user)
                .orderTime(LocalDateTime.now())
                .orderState(OrderStatus.PENDING)
                .build();

        for (RequestSingleProduct requestSingleProduct : requestSingleProducts){
            System.out.println(requestSingleProduct.getAmount());
            Optional<Product> productExists = productService.getProductById(requestSingleProduct.getProductId());
            if (!productExists.isPresent()){
                throw new IllegalStateException("A given product does not exist");
            }
            if (requestSingleProduct.getAmount()<1 || requestSingleProduct.getAmount()>10){
                throw new IllegalStateException("Invalid count of a single product provided");
            }
            OrderSingleProduct finalProduct = OrderSingleProduct.builder()
                    .order(order)
                    .product(productExists.get())
                    .quantity(requestSingleProduct.getAmount())
                    .build();
            finalOrderProducts.add(finalProduct);
        }

        order.setCartProducts(finalOrderProducts);
        orderRepository.save(order);
        orderSingleProductRepository.saveAll(finalOrderProducts);
    }
}
