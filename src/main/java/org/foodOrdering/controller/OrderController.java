package org.foodOrdering.controller;

import org.foodOrdering.dtos.OrderRequestDTO;
import org.foodOrdering.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/order")
    public void placeOrder(@RequestHeader Long userId, @RequestBody List<OrderRequestDTO> orderRequestDTOList) {
        orderService.placeOrder(userId, orderRequestDTOList);
    }
}
