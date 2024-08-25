package org.foodOrdering.mapper;

import org.foodOrdering.dtos.RestaurantMenuItemDTO;
import org.foodOrdering.model.RestaurantMenuItem;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RestaurantMenuItemMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "menuItemId", source = "menuItem.id")
    @Mapping(target = "restaurantId", source = "restaurant.id")
    RestaurantMenuItemDTO toDTO(RestaurantMenuItem restaurantMenuItem);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "restaurantMenuItemDTO.menuItemId", target = "menuItem.id")
    @Mapping(source = "restaurantId", target = "restaurant.id")
    RestaurantMenuItem toEntity(RestaurantMenuItemDTO restaurantMenuItemDTO, Long restaurantId);

    List<RestaurantMenuItemDTO> toDTOList(List<RestaurantMenuItem> restaurantMenuItems);
}

