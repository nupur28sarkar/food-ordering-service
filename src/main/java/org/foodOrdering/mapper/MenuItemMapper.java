package org.foodOrdering.mapper;

import org.foodOrdering.dtos.MenuItemDTO;
import org.foodOrdering.dtos.RestaurantDTO;
import org.foodOrdering.model.MenuItem;
import org.foodOrdering.model.Restaurant;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MenuItemMapper {

    MenuItem toEntity(MenuItemDTO menuItemDTO);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    List<MenuItemDTO> toDTO(List<MenuItem> menuItem);
}
