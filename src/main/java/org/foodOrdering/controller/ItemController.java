package org.foodOrdering.controller;

import lombok.RequiredArgsConstructor;
import org.foodOrdering.dtos.RestaurantDTO;
import org.foodOrdering.model.MenuItem;
import org.foodOrdering.service.MenuItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/item")
@RequiredArgsConstructor
public class ItemController {

    private final MenuItemService menuItemService;
    @PostMapping
    public ResponseEntity<?> addItems(@RequestBody List<MenuItem> menuItemList) {
        menuItemService.addMenuItems(menuItemList);
        return ResponseEntity.ok().body("Items add successfully.");
    }
}
