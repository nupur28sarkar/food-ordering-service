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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantMenuServiceImpl implements RestaurantMenuItemService {

    private final RestaurantMenuItemRepository restaurantMenuItemRepository;

    private final RestaurantRepository restaurantRepository;

    private final RestaurantMenuItemMapper restaurantMenuItemMapper;
    @Override
    public List<RestaurantMenuItem> addMenuItem(Long restaurantId, List<RestaurantMenuItemDTO> restaurantMenu) {
        Optional<Restaurant> restaurantOptional = restaurantRepository.findById(restaurantId);
        if (restaurantOptional.isEmpty()) {
            throw new EntityNotFoundException("Restaurant with id " + restaurantId + " not found");
        }
        List<RestaurantMenuItem> restaurantMenuItemList = restaurantMenuItemRepository.findAllByRestaurantId(restaurantId);
        Set<Long> existingItemIds = restaurantMenuItemList.stream()
                .map(item -> item.getMenuItem().getId())
                .collect(Collectors.toSet());
        List<RestaurantMenuItem> restaurantMenuItemsToSave = restaurantMenu.stream()
                .filter(dto -> !existingItemIds.contains(dto.getMenuItemId()))
                .map((RestaurantMenuItemDTO restaurantMenuItemDTO) -> restaurantMenuItemMapper.toEntity(restaurantMenuItemDTO, restaurantId)) // Convert DTOs to entities with additional parameter
                .toList();

        return restaurantMenuItemRepository.saveAll(restaurantMenuItemsToSave);
    }

    @Override
    public List<RestaurantMenuItemDTO> updateMenuItem(Long restaurantId, List<RestaurantMenuItemDTO> updatedMenu) {
        return null;
    }
}
