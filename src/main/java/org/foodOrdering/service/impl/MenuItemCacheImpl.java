package org.foodOrdering.service.impl;

import lombok.RequiredArgsConstructor;
import org.foodOrdering.dtos.MenuItemDTO;
import org.foodOrdering.mapper.MenuItemMapper;
import org.foodOrdering.model.MenuItem;
import org.foodOrdering.service.MenuItemService;
import org.foodOrdering.service.RedisService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("cacheMenuItemService")
@RequiredArgsConstructor
public class MenuItemCacheImpl implements MenuItemService {

    private final RedisService redisService;

    @Qualifier("realMenuItemService")
    private final MenuItemService realMenuItemService;

    private final MenuItemMapper menuItemMapper;


    @Override
    public List<MenuItemDTO> addMenuItems(List<MenuItem> menuItemList) {
        // Delegate to real service
        List<MenuItemDTO> updatedMenuItemList = realMenuItemService.addMenuItems(menuItemList);
        redisService.updateMenuCache(updatedMenuItemList);
        return updatedMenuItemList;
    }

    @Override
    public List<MenuItemDTO> getMenuItems() {
        List<MenuItemDTO> menuItemList = redisService.getMenuItemsFromCache();

        if (menuItemList == null || menuItemList.isEmpty()) {
            // Fetch from the database if not in cache
            menuItemList = new ArrayList<>(realMenuItemService.getMenuItems());
            redisService.updateMenuCache(menuItemList);
        }
        return menuItemList;
    }
}
