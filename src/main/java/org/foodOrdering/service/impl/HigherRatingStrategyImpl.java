package org.foodOrdering.service.impl;

import org.foodOrdering.dtos.RestaurantOrderItem;
import org.foodOrdering.model.RestaurantMenuItem;
import org.foodOrdering.service.RestaurantSelectionStrategy;

import java.util.List;

public class HigherRatingStrategyImpl implements RestaurantSelectionStrategy {
    @Override
    public List<RestaurantOrderItem> selectRestaurant(List<RestaurantMenuItem> restaurants, Double quantity) {
        return null;
    }
}
