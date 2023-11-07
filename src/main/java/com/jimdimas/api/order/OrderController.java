package com.jimdimas.api.order;

import com.jimdimas.api.exception.BadRequestException;
import com.jimdimas.api.exception.NotFoundException;
import com.jimdimas.api.user.User;
import com.jimdimas.api.util.JsonResponse;
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
    public ResponseEntity<JsonResponse> addOrder(
            @RequestAttribute(name="user") User user,
            @RequestBody List<RequestSingleProduct> requestSingleProducts //check RequestSingleProduct class for explanation
    ) throws MessagingException, NotFoundException, BadRequestException {
        return ResponseEntity.ok(orderService.addOrder(user, requestSingleProducts));
    }

    @GetMapping("/verifyOrder")
    public ResponseEntity<JsonResponse> verifyOrder(
            @RequestParam(name = "email") String email,
            @RequestParam(name="orderId") UUID orderId,
            @RequestParam(name="token") String token) throws NotFoundException, BadRequestException {
        return ResponseEntity.ok(orderService.verifyOrder(email,orderId,token));
    }

    @GetMapping(path="{orderId}")
    public Optional<Order> getOrderById(
            @RequestAttribute(name="user") User user,
            @PathVariable UUID orderId
    ) throws NotFoundException {
        return orderService.getOrderById(user,orderId);
    }

    @PutMapping(path="{orderId}")
    public ResponseEntity<JsonResponse> updateOrder(
            @RequestAttribute(name="user") User user,
            @PathVariable UUID orderId,
            @RequestBody List<RequestSingleProduct> requestSingleProducts
    ) throws MessagingException, NotFoundException, BadRequestException {
        return ResponseEntity.ok(orderService.updateOrder(user,orderId,requestSingleProducts));
    }
}
