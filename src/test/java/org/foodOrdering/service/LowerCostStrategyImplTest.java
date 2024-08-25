package org.foodOrdering.service;

import org.foodOrdering.dtos.RestaurantOrderItem;
import org.foodOrdering.model.Restaurant;
import org.foodOrdering.model.RestaurantMenuItem;
import org.foodOrdering.service.RedisService;
import org.foodOrdering.service.RestaurantSelectionStrategy;
import org.foodOrdering.service.impl.LowerCostStrategyImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LowerCostStrategyImplTest {

    @Mock
    private RedisService redisService;

    @InjectMocks
    private LowerCostStrategyImpl lowerCostStrategy;

    private Restaurant restaurant1;
    private Restaurant restaurant2;
    private RestaurantMenuItem menuItem1;
    private RestaurantMenuItem menuItem2;

    @BeforeEach
    void setUp() {
        restaurant1 = new Restaurant();
        restaurant1.setId(1L);

        restaurant2 = new Restaurant();
        restaurant2.setId(2L);

        menuItem1 = new RestaurantMenuItem();
        menuItem1.setId(1L);
        menuItem1.setPrice(10.0);
        menuItem1.setRestaurant(restaurant1);

        menuItem2 = new RestaurantMenuItem();
        menuItem2.setId(2L);
        menuItem2.setPrice(15.0);
        menuItem2.setRestaurant(restaurant2);
    }

    @Test
    void testSelectRestaurant_SufficientCapacity() {
        when(redisService.getCapacity(restaurant1.getId())).thenReturn(5.0);
//        when(redisService.getCapacity(restaurant2.getId())).thenReturn(10.0);
        when(redisService.reserveCapacity(restaurant1.getId(), 5.0)).thenReturn(true);

        List<RestaurantOrderItem> result = lowerCostStrategy.selectRestaurant(List.of(menuItem1, menuItem2), 5.0);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(menuItem1, result.get(0).getMenuItem());
        assertEquals(5.0, result.get(0).getQuantity());

        verify(redisService, times(1)).reserveCapacity(restaurant1.getId(), 5.0);
    }

    @Test
    void testSelectRestaurant_InsufficientCapacity() {
        when(redisService.getCapacity(restaurant1.getId())).thenReturn(3.0);
        when(redisService.getCapacity(restaurant2.getId())).thenReturn(1.0);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                lowerCostStrategy.selectRestaurant(List.of(menuItem1, menuItem2), 5.0));

        assertEquals("Insufficient capacity to fulfill the order.", exception.getMessage());
        verify(redisService, never()).reserveCapacity(anyLong(), anyDouble());
    }

    @Test
    void testSelectRestaurant_ReservationFails() {
        when(redisService.getCapacity(restaurant1.getId())).thenReturn(5.0);
        when(redisService.reserveCapacity(restaurant1.getId(), 5.0)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                lowerCostStrategy.selectRestaurant(List.of(menuItem1, menuItem2), 5.0));

        assertEquals("Unable to reserve capacity for restaurant: " + restaurant1.getId(), exception.getMessage());
        verify(redisService, times(1)).reserveCapacity(restaurant1.getId(), 5.0);
    }

    @Test
    void testSelectRestaurant_MultipleRestaurants() {
        when(redisService.getCapacity(restaurant1.getId())).thenReturn(3.0);
        when(redisService.getCapacity(restaurant2.getId())).thenReturn(4.0);
        when(redisService.reserveCapacity(restaurant1.getId(), 3.0)).thenReturn(true);
        when(redisService.reserveCapacity(restaurant2.getId(), 2.0)).thenReturn(true);

        List<RestaurantOrderItem> result = lowerCostStrategy.selectRestaurant(List.of(menuItem1, menuItem2), 5.0);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(menuItem1, result.get(0).getMenuItem());
        assertEquals(3.0, result.get(0).getQuantity());
        assertEquals(menuItem2, result.get(1).getMenuItem());
        assertEquals(2.0, result.get(1).getQuantity());

        verify(redisService, times(1)).reserveCapacity(restaurant1.getId(), 3.0);
        verify(redisService, times(1)).reserveCapacity(restaurant2.getId(), 2.0);
    }
}
