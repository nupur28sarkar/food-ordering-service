package org.foodOrdering.service;

import org.foodOrdering.dtos.RestaurantOrderItem;
import org.foodOrdering.model.Restaurant;
import org.foodOrdering.model.RestaurantMenuItem;
import org.foodOrdering.service.RedisService;
import org.foodOrdering.service.impl.LowerCostStrategyImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LowerCostStrategyImplTest {

    @Mock
    private RedisService redisService;

    @InjectMocks
    private LowerCostStrategyImpl lowerCostStrategy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void selectRestaurant_Success() {
        // Arrange
        Restaurant restaurant1 = new Restaurant();
        restaurant1.setId(1L);

        Restaurant restaurant2 = new Restaurant();
        restaurant2.setId(2L);

        RestaurantMenuItem item1 = new RestaurantMenuItem();
        item1.setRestaurant(restaurant1);
        item1.setPrice(10.0);

        RestaurantMenuItem item2 = new RestaurantMenuItem();
        item2.setRestaurant(restaurant2);
        item2.setPrice(15.0);

        List<RestaurantMenuItem> restaurantMenuItems = List.of(item1, item2);

        when(redisService.getCapacity(1L)).thenReturn(5.0);
        when(redisService.getCapacity(2L)).thenReturn(5.0);
        when(redisService.reserveCapacity(1L, 5.0)).thenReturn(true);
        when(redisService.reserveCapacity(2L, 5.0)).thenReturn(true);

        // Act
        List<RestaurantOrderItem> result = lowerCostStrategy.selectRestaurant(restaurantMenuItems, 10.0);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(item1, result.get(0).getMenuItem());
        assertEquals(5.0, result.get(0).getQuantity());
        assertEquals(item2, result.get(1).getMenuItem());
        assertEquals(5.0, result.get(1).getQuantity());

        verify(redisService, times(1)).getCapacity(1L);
        verify(redisService, times(1)).getCapacity(2L);
        verify(redisService, times(1)).reserveCapacity(1L, 5.0);
        verify(redisService, times(1)).reserveCapacity(2L, 5.0);
    }

    @Test
    void selectRestaurant_InsufficientCapacity() {
        // Arrange
        Restaurant restaurant1 = new Restaurant();
        restaurant1.setId(1L);

        Restaurant restaurant2 = new Restaurant();
        restaurant2.setId(2L);

        RestaurantMenuItem item1 = new RestaurantMenuItem();
        item1.setRestaurant(restaurant1);
        item1.setPrice(10.0);

        RestaurantMenuItem item2 = new RestaurantMenuItem();
        item2.setRestaurant(restaurant2);
        item2.setPrice(15.0);

        List<RestaurantMenuItem> restaurantMenuItems = List.of(item1, item2);

        when(redisService.getCapacity(1L)).thenReturn(2.0);
        when(redisService.getCapacity(2L)).thenReturn(2.0);
        when(redisService.reserveCapacity(1L, 2.0)).thenReturn(true);
        when(redisService.reserveCapacity(2L, 2.0)).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            lowerCostStrategy.selectRestaurant(restaurantMenuItems, 10.0);
        });

        assertEquals("Insufficient capacity to fulfill the order.", exception.getMessage());

        verify(redisService, times(1)).getCapacity(1L);
        verify(redisService, times(1)).getCapacity(2L);
        verify(redisService, times(1)).reserveCapacity(1L, 2.0);
        verify(redisService, times(1)).reserveCapacity(2L, 2.0);
    }

    @Test
    void selectRestaurant_ReserveCapacityFails() {
        // Arrange
        Restaurant restaurant1 = new Restaurant();
        restaurant1.setId(1L);

        RestaurantMenuItem item1 = new RestaurantMenuItem();
        item1.setRestaurant(restaurant1);
        item1.setPrice(10.0);

        List<RestaurantMenuItem> restaurantMenuItems = List.of(item1);

        when(redisService.getCapacity(1L)).thenReturn(5.0);
        when(redisService.reserveCapacity(1L, 5.0)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            lowerCostStrategy.selectRestaurant(restaurantMenuItems, 5.0);
        });

        assertEquals("Unable to reserve capacity for restaurant: 1", exception.getMessage());

        verify(redisService, times(1)).getCapacity(1L);
        verify(redisService, times(1)).reserveCapacity(1L, 5.0);
    }

    @Test
    void selectRestaurant_NoAvailableRestaurants() {
        // Arrange
        List<RestaurantMenuItem> restaurantMenuItems = List.of();

        // Act
        List<RestaurantOrderItem> result = lowerCostStrategy.selectRestaurant(restaurantMenuItems, 10.0);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
