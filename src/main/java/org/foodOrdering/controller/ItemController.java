package org.foodOrdering.controller;

import lombok.RequiredArgsConstructor;
import org.foodOrdering.dtos.MenuItemDTO;
import org.foodOrdering.dtos.RestaurantDTO;
import org.foodOrdering.model.MenuItem;
import org.foodOrdering.service.MenuItemService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/item")
public class ItemController {

    private final MenuItemService menuItemService;

    public ItemController(@Qualifier("cacheMenuItemService") MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }
    @PostMapping
    public ResponseEntity<?> addItems(@RequestBody List<MenuItem> menuItemList) {
        menuItemService.addMenuItems(menuItemList);
        return ResponseEntity.ok().body("Items add successfully.");
    }

    @GetMapping
    public ResponseEntity<List<MenuItemDTO>> getItems() {
       List<MenuItemDTO> menuItemDTOList =  menuItemService.getMenuItems();
        return new ResponseEntity<>(menuItemDTOList, HttpStatus.OK);
    }
}
