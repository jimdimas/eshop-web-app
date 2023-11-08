package com.jimdimas.api.order;

import com.jimdimas.api.email.ApplicationEmailService;
import com.jimdimas.api.exception.BadRequestException;
import com.jimdimas.api.exception.NotFoundException;
import com.jimdimas.api.product.Product;
import com.jimdimas.api.product.ProductService;
import com.jimdimas.api.user.Role;
import com.jimdimas.api.user.User;
import com.jimdimas.api.util.JsonResponse;
import com.jimdimas.api.util.UtilService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
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
    public JsonResponse addOrder(User user, List<RequestSingleProduct> requestSingleProducts) throws MessagingException, NotFoundException, BadRequestException {
        List<OrderSingleProduct> finalOrderProducts = new ArrayList<>();
        Order order = Order.builder()
                .orderId(UUID.randomUUID())
                .user(user)
                .orderTime(LocalDateTime.now())
                .orderState(OrderState.PENDING)
                .build();

        for (RequestSingleProduct requestSingleProduct : requestSingleProducts){
            Optional<Product> productExists = productService.getProductById(requestSingleProduct.getProductId());
            if (!productExists.isPresent()){
                throw new NotFoundException("A given product does not exist");
            }
            if (requestSingleProduct.getQuantity()<1 || requestSingleProduct.getQuantity()>10){
                throw new BadRequestException("Invalid count of a single product provided");
            }
            OrderSingleProduct finalProduct = OrderSingleProduct.builder()
                    .order(order)
                    .product(productExists.get())
                    .quantity(requestSingleProduct.getQuantity())
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
        return JsonResponse.builder().message("Order has been created , please verify by email to continue.").build();
    }

    public JsonResponse verifyOrder(String email, UUID orderId, String token) throws NotFoundException, BadRequestException {
        Optional<Order> orderExists = orderRepository.findByPublicId(orderId);
        if (!orderExists.isPresent()){
            throw new NotFoundException("Order verification failed");
        }
        Order order = orderExists.get();
        if (!order.getUser().getEmail().equals(email) || !order.getVerificationToken().equals(token)){
            throw new BadRequestException("Order verification failed");
        }
        order.setOrderState(OrderState.VERIFIED);
        order.setVerificationToken("");
        orderRepository.save(order);
        return JsonResponse.builder().message("Order verification was successful").build();
    }

    public Optional<Order> getOrderById(User user, UUID orderId) throws NotFoundException {
        Optional<Order> orderExists = orderRepository.findByPublicId(orderId);
        if (!orderExists.isPresent() || !user.getUsername().equals(orderExists.get().getUser().getUsername())){
            throw new NotFoundException("Given user does not have an order with given id");
        }
        return Optional.ofNullable(orderExists.get());
    }
    /*  This method is transactional as well.We need either all the changes to be committed or none , otherwise we are
    going to have an inconsistent database.The update order method gets a list of all the cart products that must be kept on the order,
    along with changes of the quantities of certain products.If a product is not provided, that means we want it to be deleted from the previous
    order.
    * */
    @Transactional
    public JsonResponse updateOrder(User user,UUID orderId,List<RequestSingleProduct> requestSingleProducts) throws MessagingException, NotFoundException, BadRequestException {
        Optional<Order> orderExists = orderRepository.findByPublicId(orderId);
        if (!orderExists.isPresent()){
            throw new NotFoundException("You have no order with given id.");
        }
        Order updatedOrder = orderExists.get();
        if (!updatedOrder.getUser().getUsername().equals(user.getUsername())){
            throw new NotFoundException("You have no order with given id.");
        }
        if (updatedOrder.getOrderState().equals(OrderState.PENDING)){
            throw new BadRequestException("You need to verify your order first in order to update it.");
        }

        List<OrderSingleProduct> updatedOrderProducts = new ArrayList<>();
        for (RequestSingleProduct requestSingleProduct:requestSingleProducts){
            if (requestSingleProduct.getQuantity()<1 || requestSingleProduct.getQuantity()>10){
                throw new BadRequestException("Invalid count of product with id : "+requestSingleProduct.getProductId()+" provided");
            }

            Optional<OrderSingleProduct> productExistsInOrder = orderSingleProductRepository.findProductInOrder(orderId,requestSingleProduct.getProductId());
            if (!productExistsInOrder.isPresent()){
                throw new BadRequestException("Product with id "+requestSingleProduct.getProductId()+" does not exist in previous order,cannot update");
            }
            OrderSingleProduct updatedOrderProduct = productExistsInOrder.get();
            if (!updatedOrderProduct.getQuantity().equals(requestSingleProduct.getQuantity()))  //if quantity was changed , update it
            {
                updatedOrderProduct.setQuantity(requestSingleProduct.getQuantity());
            }
            updatedOrderProducts.add(updatedOrderProduct);
        }

        List<OrderSingleProduct> toBeDeletedOrderProducts = updatedOrder.getCartProducts();
        toBeDeletedOrderProducts.removeAll(updatedOrderProducts);
        orderSingleProductRepository.deleteAll(toBeDeletedOrderProducts); //delete all products that were not included in the new received list
        orderSingleProductRepository.saveAll(updatedOrderProducts);
        updatedOrder.setCartProducts(updatedOrderProducts);
        updatedOrder.setVerificationToken(utilService.getSecureRandomToken(32));
        updatedOrder.setOrderState(OrderState.PENDING);
        orderRepository.save(updatedOrder);
        emailService.sendOrderUpdateVerificationMail(user.getEmail(),updatedOrder);
        return JsonResponse.builder().message("Order with id : "+orderId.toString()+" has been updated successfully,please check your email for verification.").build();
    }

    @Transactional
    public JsonResponse deleteOrder(User user,UUID orderId) throws NotFoundException, BadRequestException {
        Optional<Order> orderExists = orderRepository.findByPublicId(orderId);
        if (!orderExists.isPresent()){
            throw new NotFoundException("You have no order with id : "+orderId.toString()+" exists");
        }
        Order order = orderExists.get();
        if (!order.getUser().getUsername().equals(user.getUsername())){
            throw new NotFoundException("You have no order with id : "+orderId.toString()+" exists");
        }

        orderSingleProductRepository.deleteAll(order.getCartProducts());
        orderRepository.delete(order);
        return JsonResponse.builder().message("Order with id : "+orderId.toString()+" has been deleted successfully").build();
    }
}
