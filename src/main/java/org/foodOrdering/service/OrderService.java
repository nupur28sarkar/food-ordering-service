package org.foodOrdering.service;

import org.foodOrdering.dtos.OrderRequestDTO;
import org.foodOrdering.dtos.OrderResponseDTO;

import java.util.List;

public interface OrderService {
    OrderResponseDTO placeOrder(Long userId, List<OrderRequestDTO> itemQuantities);
}
