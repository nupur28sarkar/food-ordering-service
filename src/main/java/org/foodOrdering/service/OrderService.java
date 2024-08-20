package org.foodOrdering.service;

import org.foodOrdering.dtos.OrderRequestDTO;

import java.util.List;

public interface OrderService {
    void placeOrder(Long userId, List<OrderRequestDTO> itemQuantities);
}
