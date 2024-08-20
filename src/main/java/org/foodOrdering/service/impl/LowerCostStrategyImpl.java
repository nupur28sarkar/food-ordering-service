package org.foodOrdering.service.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.foodOrdering.dtos.RestaurantOrderItem;
import org.foodOrdering.model.OrderItem;
import org.foodOrdering.model.RestaurantMenuItem;
import org.foodOrdering.service.RedisService;
import org.foodOrdering.service.RestaurantSelectionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class LowerCostStrategyImpl implements RestaurantSelectionStrategy {

    private final RedisService redisService;
    @Override
    public List<RestaurantOrderItem> selectRestaurant(List<RestaurantMenuItem> restaurants, Double quantity) {
        List<RestaurantOrderItem> orderItems = new ArrayList<>();
        double remainingQuantity = quantity;

        // Sort restaurants by price
        List<RestaurantMenuItem> sortedRestaurants = restaurants.stream()
                .filter(item -> item.getPrice() != null)
                .sorted(Comparator.comparing(RestaurantMenuItem::getPrice))
                .toList();

        for (RestaurantMenuItem item : sortedRestaurants) {
            if (remainingQuantity <= 0) break;

            double availableCapacity = redisService.getCapacity(item.getRestaurant().getId());
            double quantityToOrder = Math.min(remainingQuantity, availableCapacity);

            if (quantityToOrder > 0) {
                orderItems.add(new RestaurantOrderItem(item, quantityToOrder));
                remainingQuantity -= quantityToOrder;

                // Update Redis with the reserved capacity
                boolean reserved = redisService.reserveCapacity(item.getRestaurant().getId(), quantityToOrder);
                if (!reserved) {
                    throw new RuntimeException("Unable to reserve capacity for restaurant: " + item.getRestaurant().getId());
                }
            }
        }

        if (remainingQuantity > 0) {
            throw new RuntimeException("Insufficient capacity to fulfill the order.");
        }

        return orderItems;
    }

}
