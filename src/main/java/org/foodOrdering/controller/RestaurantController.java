package org.foodOrdering.controller;

import org.foodOrdering.dtos.RestaurantDTO;
import org.foodOrdering.dtos.RestaurantMenuItemDTO;
import org.foodOrdering.service.RestaurantMenuItemService;
import org.foodOrdering.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private RestaurantMenuItemService restaurantMenuItemService;

    @PostMapping
    public ResponseEntity<RestaurantDTO> registerRestaurant(@RequestBody RestaurantDTO restaurant) {
        RestaurantDTO createdRestaurant = restaurantService.registerRestaurant(restaurant);
        return new ResponseEntity<>(createdRestaurant, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantDTO> updateRestaurant(@PathVariable Long id, @RequestBody RestaurantDTO updatedRestaurant) {
        RestaurantDTO restaurant = restaurantService.updateRestaurant(id, updatedRestaurant);
        return new ResponseEntity<>(restaurant, HttpStatus.OK);
    }

    @PostMapping("/{restaurantId}/menu")
    public ResponseEntity<List<RestaurantMenuItemDTO> > addMenuItems(@PathVariable Long restaurantId, @RequestBody List<RestaurantMenuItemDTO> restaurantMenu) {
        List<RestaurantMenuItemDTO> createdMenuItems = restaurantMenuItemService.addMenuItem(restaurantId, restaurantMenu);
        return new ResponseEntity<>(createdMenuItems, HttpStatus.CREATED);
    }

    @PutMapping("/menu/{restaurantId}")
    public ResponseEntity<List<RestaurantMenuItemDTO> > updateMenuItems(@PathVariable Long restaurantId, @RequestBody List<RestaurantMenuItemDTO> updatedMenu) {
        List<RestaurantMenuItemDTO> updatedRestaurantMenuItems  = restaurantMenuItemService.updateMenuItem(restaurantId, updatedMenu);
        return new ResponseEntity<>(updatedRestaurantMenuItems, HttpStatus.OK);
    }
}
