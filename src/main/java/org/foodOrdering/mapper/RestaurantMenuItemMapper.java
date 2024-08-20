package org.foodOrdering.mapper;

import org.foodOrdering.dtos.RestaurantMenuItemDTO;
import org.foodOrdering.model.RestaurantMenuItem;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface RestaurantMenuItemMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "itemId", source = "restaurantMenuItem.menuItem.id")
    RestaurantMenuItemDTO toDTO(RestaurantMenuItem restaurantMenuItem);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "itemId", target = "menuItem.id")
    RestaurantMenuItem toEntity(RestaurantMenuItemDTO restaurantMenuItemDTO);
}
