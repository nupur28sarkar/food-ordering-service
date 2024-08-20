package org.foodOrdering.service.impl;

import lombok.RequiredArgsConstructor;
import org.foodOrdering.dtos.RestaurantMenuItemDTO;
import org.foodOrdering.exception.EntityNotFoundException;
import org.foodOrdering.mapper.RestaurantMenuItemMapper;
import org.foodOrdering.model.Restaurant;
import org.foodOrdering.model.RestaurantMenuItem;
import org.foodOrdering.repositories.RestaurantMenuItemRepository;
import org.foodOrdering.repositories.RestaurantRepository;
import org.foodOrdering.service.RestaurantMenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantMenuServiceImpl implements RestaurantMenuItemService {

    @Autowired
    private final RestaurantMenuItemRepository restaurantMenuItemRepository;

    @Autowired
    private final RestaurantRepository restaurantRepository;

    @Autowired
    private final RestaurantMenuItemMapper restaurantMenuItemMapper;
    @Override
    public List<RestaurantMenuItemDTO> addMenuItem(Long restaurantId, List<RestaurantMenuItemDTO> restaurantMenuDTOs) {
        Optional<Restaurant> restaurantOptional = restaurantRepository.findById(restaurantId);
        if (restaurantOptional.isEmpty()) {
            throw new EntityNotFoundException("Restaurant with id " + restaurantId + " not found");
        }
        List<RestaurantMenuItem> restaurantMenuItemList = restaurantMenuItemRepository.findAllByRestaurantId(restaurantId);
        Set<Long> existingItemIds = restaurantMenuItemList.stream()
                .map(item -> item.getMenuItem().getId())
                .collect(Collectors.toSet());
        List<RestaurantMenuItemDTO> filteredDTOs = restaurantMenuDTOs.stream()
                .filter(dto -> !existingItemIds.contains(dto.getItemId()))
                .toList();
        List<RestaurantMenuItem> newRestaurantMenuItems = filteredDTOs.stream()
                .map(dto -> {
                    RestaurantMenuItem entity = restaurantMenuItemMapper.toEntity(dto);
                    entity.setRestaurant(restaurantOptional.get());
                    return entity;
                })
                .toList();
        return restaurantMenuItemRepository.saveAll(newRestaurantMenuItems).stream()
                .map(restaurantMenuItemMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RestaurantMenuItemDTO> updateMenuItem(Long restaurantId, List<RestaurantMenuItemDTO> updatedMenu) {
        return null;
    }
}
