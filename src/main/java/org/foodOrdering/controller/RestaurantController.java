package org.foodOrdering.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.foodOrdering.dtos.DispatchUpdateDTO;
import org.foodOrdering.dtos.RestaurantDTO;
import org.foodOrdering.dtos.RestaurantMenuItemDTO;
import org.foodOrdering.model.RestaurantMenuItem;
import org.foodOrdering.service.RestaurantMenuItemService;
import org.foodOrdering.service.RestaurantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    private final RestaurantMenuItemService restaurantMenuItemService;

    @PostMapping
    public ResponseEntity<RestaurantDTO> registerRestaurant(@RequestBody RestaurantDTO restaurantDTO) {
        RestaurantDTO createdRestaurant = restaurantService.registerRestaurant(restaurantDTO);
        return new ResponseEntity<>(createdRestaurant, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantDTO> updateRestaurant(@PathVariable Long id, @RequestBody RestaurantDTO restaurantDTO) {
        RestaurantDTO restaurant = restaurantService.updateRestaurant(id, restaurantDTO);
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

    @PostMapping("/{restaurantId}/dispatch")
    public ResponseEntity<?> dispatchItems(@PathVariable Long restaurantId, @RequestBody List<DispatchUpdateDTO> dispatchRequests) {
        restaurantService.dispatchItems(restaurantId, dispatchRequests);
        return ResponseEntity.ok().body("Items dispatched successfully.");
    }
}
