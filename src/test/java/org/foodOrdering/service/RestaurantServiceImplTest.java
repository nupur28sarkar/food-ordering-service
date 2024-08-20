package org.foodOrdering.service;

import org.foodOrdering.dtos.RestaurantDTO;
import org.foodOrdering.exception.EntityNotFoundException;
import org.foodOrdering.mapper.RestaurantMapper;
import org.foodOrdering.model.Restaurant;
import org.foodOrdering.repositories.RestaurantRepository;
import org.foodOrdering.service.impl.RestaurantServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import org.foodOrdering.exception.EntityAlreadyExistsException;

import java.util.Optional;

public class RestaurantServiceImplTest {
    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantMapper restaurantMapper;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private RestaurantServiceImpl restaurantService;

    public RestaurantServiceImplTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerRestaurant_Success() {
        RestaurantDTO restaurantDTO = new RestaurantDTO();
        Restaurant restaurant = new Restaurant();

        when(restaurantRepository.existsByNameOrEmail(anyString(), anyString())).thenReturn(false);
        when(restaurantMapper.toEntity(any(RestaurantDTO.class))).thenReturn(restaurant);
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);
        when(restaurantMapper.toDTO(any(Restaurant.class))).thenReturn(restaurantDTO);

        RestaurantDTO result = restaurantService.registerRestaurant(restaurantDTO);

        verify(redisService, times(1)).initializeCapacity(anyLong(), (int) anyDouble());
        assertEquals(restaurantDTO, result);
    }

    @Test
    void registerRestaurant_AlreadyExists() {
        when(restaurantRepository.existsByNameOrEmail(anyString(), anyString())).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class, () -> {
            restaurantService.registerRestaurant(new RestaurantDTO());
        });

        verify(redisService, never()).initializeCapacity(anyLong(), (int) anyDouble());
    }

    @Test
    void updateRestaurant_Success() {
        RestaurantDTO restaurantDTO = new RestaurantDTO();
        Restaurant restaurant = new Restaurant();

        when(restaurantRepository.findById(anyLong())).thenReturn(Optional.of(restaurant));
        when(restaurantMapper.toEntity(any(RestaurantDTO.class))).thenReturn(restaurant);
        when(restaurantMapper.toDTO(any(Restaurant.class))).thenReturn(restaurantDTO);

        RestaurantDTO result = restaurantService.updateRestaurant(1L, restaurantDTO);

        assertEquals(restaurantDTO, result);
    }

    @Test
    void updateRestaurant_NotFound() {
        when(restaurantRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            restaurantService.updateRestaurant(1L, new RestaurantDTO());
        });
    }
}
