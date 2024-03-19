package com.stevemd.orderservice.controller;

import com.stevemd.orderservice.dto.OrderRequest;
import com.stevemd.orderservice.dto.OrderResponse;
import com.stevemd.orderservice.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {


    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/place")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse placeOrder(@RequestBody OrderRequest orderRequest) {
         orderService.placeOrder(orderRequest);
         return OrderResponse.builder()
                 .status("0")
                 .message("Order placed successfully")
                 .build();
    }
}
