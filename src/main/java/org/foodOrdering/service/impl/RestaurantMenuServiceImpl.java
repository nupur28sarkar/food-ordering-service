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

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantMenuServiceImpl implements RestaurantMenuItemService {

    private final RestaurantMenuItemRepository restaurantMenuItemRepository;

    private final RestaurantRepository restaurantRepository;

    private final RestaurantMenuItemMapper restaurantMenuItemMapper;
    @Override
    public List<RestaurantMenuItemDTO> addMenuItem(Long restaurantId, List<RestaurantMenuItemDTO> restaurantMenu) {
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

        List<RestaurantMenuItem> savedRestaurantMenuItem =  restaurantMenuItemRepository.saveAll(restaurantMenuItemsToSave);
        return restaurantMenuItemMapper.toDTOList(savedRestaurantMenuItem);
    }

    @Override
    public List<RestaurantMenuItemDTO> updateMenuItem(Long restaurantId, List<RestaurantMenuItemDTO> updatedMenu) {
        Optional<Restaurant> restaurantOptional = restaurantRepository.findById(restaurantId);
        if (restaurantOptional.isEmpty()) {
            throw new EntityNotFoundException("Restaurant with id " + restaurantId + " not found");
        }

        // Fetch existing menu items for the restaurant
        List<RestaurantMenuItem> existingMenuItems = restaurantMenuItemRepository.findAllByRestaurantId(restaurantId);

        // Map existing items by menuItemId for quick lookup
        Map<Long, RestaurantMenuItem> existingMenuItemMap = existingMenuItems.stream()
                .collect(Collectors.toMap(RestaurantMenuItem::getId, item -> item));

        // Prepare list to save
        List<RestaurantMenuItem> menuItemsToSave = new ArrayList<>();

        // Iterate through updated menu items
        for (RestaurantMenuItemDTO dto : updatedMenu) {
            RestaurantMenuItem existingItem = existingMenuItemMap.get(dto.getId());

            if (existingItem != null) {
                // Update existing item
                existingItem.setPrice(dto.getPrice());
                menuItemsToSave.add(existingItem);
            } else {
                // Create a new item if it does not exist
                RestaurantMenuItem newItem = restaurantMenuItemMapper.toEntity(dto, restaurantId);
                menuItemsToSave.add(newItem);
            }
        }

        // Save updated items
        List<RestaurantMenuItem> savedItems = restaurantMenuItemRepository.saveAll(menuItemsToSave);

        // Convert saved entities to DTOs and return
        return restaurantMenuItemMapper.toDTOList(savedItems);
    }

}
