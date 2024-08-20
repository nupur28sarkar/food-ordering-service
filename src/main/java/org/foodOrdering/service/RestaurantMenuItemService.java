package org.foodOrdering.service;

import org.foodOrdering.dtos.RestaurantMenuItemDTO;

import java.util.List;

public interface RestaurantMenuItemService {
    List<RestaurantMenuItemDTO> addMenuItem(Long id, List<RestaurantMenuItemDTO> restaurantMenu);

    List<RestaurantMenuItemDTO> updateMenuItem(Long restaurantId, List<RestaurantMenuItemDTO> updatedMenu);
}
