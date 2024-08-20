package org.foodOrdering.service;

import org.foodOrdering.dtos.DispatchUpdateDTO;
import org.foodOrdering.dtos.RestaurantDTO;

import java.util.List;

public interface RestaurantService {
    RestaurantDTO registerRestaurant(RestaurantDTO restaurant);

    RestaurantDTO updateRestaurant(Long id, RestaurantDTO updatedRestaurant);

    void dispatchItems(Long restaurantId, List<DispatchUpdateDTO> dispatchRequests);
}
