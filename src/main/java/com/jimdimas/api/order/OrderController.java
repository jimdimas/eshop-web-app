package com.jimdimas.api.order;

import com.jimdimas.api.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/order"})
@RequiredArgsConstructor
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public List<Order> getAllOrders(@RequestAttribute(name="user") User user){
        return orderService.getAllOrders(user);
    }

    @PostMapping
    public void addOrder(
            @RequestAttribute(name="user") User user,
            @RequestBody List<RequestSingleProduct> requestSingleProducts //check RequestSingleProduct class for explanation
    ){
        orderService.addOrder(user, requestSingleProducts);
    }
}
