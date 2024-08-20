package org.foodOrdering.mapper;

import org.foodOrdering.dtos.RestaurantDTO;
import org.foodOrdering.model.Restaurant;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RestaurantMapper {

    Restaurant toEntity(RestaurantDTO restaurantDTO);
    RestaurantDTO toDTO(Restaurant restaurant);
}
