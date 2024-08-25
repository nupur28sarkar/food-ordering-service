package org.foodOrdering.service.impl;

import lombok.RequiredArgsConstructor;
import org.foodOrdering.dtos.MenuItemDTO;
import org.foodOrdering.mapper.MenuItemMapper;
import org.foodOrdering.model.MenuItem;
import org.foodOrdering.repositories.MenuItemRepository;
import org.foodOrdering.service.MenuItemService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service("realMenuItemService")
@RequiredArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;

    private final MenuItemMapper menuItemMapper;
    @Override
    public List<MenuItemDTO> addMenuItems(List<MenuItem> menuItemList) {
        // Extract item names from the input list
        List<String> itemNames = menuItemList.stream()
                .map(MenuItem::getItemName)
                .collect(Collectors.toList());

        // Fetch existing items from the database in bulk
        List<MenuItem> existingItems = menuItemRepository.findByItemNameIn(itemNames);
        List<MenuItem> allItems = new ArrayList<>(existingItems);
        List<String> existingItemNames = existingItems.stream()
                .map(MenuItem::getItemName)
                .toList();

        // Determine which items are new
        List<MenuItem> newItems = menuItemList.stream()
                .filter(item -> !existingItemNames.contains(item.getItemName()))
                .collect(Collectors.toList());

        // Save new items to the database
        if (!newItems.isEmpty()) {
            menuItemRepository.saveAll(newItems);
            allItems.addAll(newItems);
        }
        return menuItemMapper.toDTO(allItems);
    }

    @Override
    public List<MenuItemDTO> getMenuItems() {
        List<MenuItem> menuItemList = menuItemRepository.findAll();
        return menuItemMapper.toDTO(menuItemList);
    }

}
