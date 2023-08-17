package com.jimdimas.api.order;

import com.jimdimas.api.email.ApplicationEmailService;
import com.jimdimas.api.product.Product;
import com.jimdimas.api.product.ProductService;
import com.jimdimas.api.user.Role;
import com.jimdimas.api.user.User;
import com.jimdimas.api.util.UtilService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final OrderSingleProductRepository orderSingleProductRepository;
    private final ApplicationEmailService emailService;
    private final UtilService utilService;
    public List<Order> getAllOrders(User user) {
        if (user.getRole().equals(Role.ADMIN)){
            return orderRepository.findAll();
        }
        else{
            return orderRepository.findByUsername(user.getUsername());
        }
    }
    /* This method is annotated with @Transactional because an order is saved first (for reference purposes) and then
    * all of it's products are saved.But there is a unique constraint on OrderSingleProduct , where a product can only exist once
    * on a certain order.In that case , if we had more than one references of a product,an order would be
    * saved in the database but it's products wouldn't.@Transactional ensures that either all products and order are saved or none of them if something fails.*/
    @Transactional
    public void addOrder(User user, List<RequestSingleProduct> requestSingleProducts) throws MessagingException {
        List<OrderSingleProduct> finalOrderProducts = new ArrayList<>();
        Order order = Order.builder()
                .orderId(UUID.randomUUID())
                .user(user)
                .orderTime(LocalDateTime.now())
                .orderState(OrderStatus.PENDING)
                .build();

        for (RequestSingleProduct requestSingleProduct : requestSingleProducts){
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
        order.setVerificationToken(utilService.getSecureRandomToken(32));

        orderRepository.save(order);
        orderSingleProductRepository.saveAll(finalOrderProducts);
        if (orderRepository.findByPublicId(order.getOrderId()).isPresent()){    //check if order was created to send email,otherwise transaction failed
            emailService.sendOrderVerificationMail(user.getEmail(), order);
        }
    }

    public String verifyOrder(String email, UUID orderId, String token) {
        Optional<Order> orderExists = orderRepository.findByPublicId(orderId);
        if (!orderExists.isPresent()){
            throw new IllegalStateException("Order verification failed");
        }
        Order order = orderExists.get();
        if (!order.getUser().getEmail().equals(email) || !order.getVerificationToken().equals(token)){
            throw new IllegalStateException("Order verification failed");
        }
        order.setOrderState(OrderStatus.VERIFIED);
        order.setVerificationToken("");
        orderRepository.save(order);
        return "Order Verification was successful";
    }
}
