package org.foodOrdering.service;

import org.foodOrdering.dtos.RestaurantOrderItem;
import org.foodOrdering.model.RestaurantMenuItem;

import java.util.List;

public interface RestaurantSelectionStrategy {
    List<RestaurantOrderItem> selectRestaurant(List<RestaurantMenuItem> restaurants, Double quantity);

}
