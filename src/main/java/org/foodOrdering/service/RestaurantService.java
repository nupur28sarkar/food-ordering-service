package org.foodOrdering.service;

import org.foodOrdering.dtos.RestaurantDTO;

public interface RestaurantService {
    RestaurantDTO registerRestaurant(RestaurantDTO restaurant);

    RestaurantDTO updateRestaurant(Long id, RestaurantDTO updatedRestaurant);
}
