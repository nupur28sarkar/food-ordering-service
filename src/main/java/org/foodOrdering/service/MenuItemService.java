package org.foodOrdering.service;

import org.foodOrdering.dtos.MenuItemDTO;
import org.foodOrdering.model.MenuItem;

import java.util.List;

public interface MenuItemService {
    List<MenuItemDTO> addMenuItems(List<MenuItem> menuItemList);

    List<MenuItemDTO> getMenuItems();
}
