package org.foodOrdering.service;

import org.foodOrdering.dtos.RestaurantMenuItemDTO;
import org.foodOrdering.model.RestaurantMenuItem;

import java.util.List;

public interface RestaurantMenuItemService {
    List<RestaurantMenuItem> addMenuItem(Long id, List<RestaurantMenuItemDTO> restaurantMenu);

    List<RestaurantMenuItemDTO> updateMenuItem(Long restaurantId, List<RestaurantMenuItemDTO> updatedMenu);
}
