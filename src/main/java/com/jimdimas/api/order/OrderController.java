package com.jimdimas.api.order;

import com.jimdimas.api.user.User;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping({"/api/v1/order"})
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<Order> getAllOrders(@RequestAttribute(name="user") User user){
        return orderService.getAllOrders(user);
    }

    @PostMapping
    public void addOrder(
            @RequestAttribute(name="user") User user,
            @RequestBody List<RequestSingleProduct> requestSingleProducts //check RequestSingleProduct class for explanation
    ) throws MessagingException {
        orderService.addOrder(user, requestSingleProducts);
    }

    @GetMapping("/verifyOrder")
    public ResponseEntity<String> verifyOrder(
            @RequestParam(name = "email") String email,
            @RequestParam(name="orderId") UUID orderId,
            @RequestParam(name="token") String token){
        return ResponseEntity.ok(orderService.verifyOrder(email,orderId,token));
    }

    @GetMapping(path="{orderId}")
    public Optional<Order> getOrderById(
            @RequestAttribute(name="user") User user,
            @PathVariable UUID orderId
    ){
        return orderService.getOrderById(user,orderId);
    }

    @PutMapping(path="{orderId}")
    public ResponseEntity<String> updateOrder(
            @RequestAttribute(name="user") User user,
            @PathVariable UUID orderId,
            @RequestBody List<RequestSingleProduct> requestSingleProducts
    ) throws MessagingException {
        return ResponseEntity.ok(orderService.updateOrder(user,orderId,requestSingleProducts));
    }
}
