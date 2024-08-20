package org.foodOrdering.service;

import org.foodOrdering.dtos.RestaurantOrderItem;
import org.foodOrdering.enums.SelectionStrategy;
import org.foodOrdering.model.RestaurantMenuItem;

import java.util.List;

public class RestaurantSelectionContext {

    private final RestaurantSelectionStrategy strategy;

    public RestaurantSelectionContext(RestaurantSelectionStrategy strategy) {
        this.strategy = strategy;
    }

    public List<RestaurantOrderItem> executeStrategy(List<RestaurantMenuItem> restaurants, Double quantity) {
        return strategy.selectRestaurant(restaurants, quantity);
    }
}
