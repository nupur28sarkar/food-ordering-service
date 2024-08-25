package org.foodOrdering.controller;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.foodOrdering.dtos.OrderRequestDTO;
import org.foodOrdering.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping()
    public ResponseEntity<?> placeOrder(@RequestHeader Long userId, @RequestBody List<OrderRequestDTO> orderRequestDTOList) {
        return new ResponseEntity<>(orderService.placeOrder(userId, orderRequestDTOList), HttpStatus.OK);
    }
}
