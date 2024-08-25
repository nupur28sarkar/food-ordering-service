package org.foodOrdering.service;

import org.foodOrdering.dtos.DispatchUpdateDTO;
import org.foodOrdering.dtos.RestaurantDTO;
import org.foodOrdering.exception.EntityAlreadyExistsException;
import org.foodOrdering.exception.EntityNotFoundException;
import org.foodOrdering.mapper.RestaurantMapper;
import org.foodOrdering.model.Restaurant;
import org.foodOrdering.repositories.RestaurantRepository;
import org.foodOrdering.service.impl.RestaurantServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceImplTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantMapper restaurantMapper;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private RestaurantServiceImpl restaurantService;

    private RestaurantDTO restaurantDTO;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        restaurantDTO = new RestaurantDTO(1L, "Test Restaurant", "123 Test Address", "test@example.com", "1234567890", 100.0);
        restaurant = Restaurant.builder()
                .id(1L)
                .name("Test Restaurant")
                .address("123 Test Address")
                .email("test@example.com")
                .phone("1234567890")
                .processingCapacity(100.0)
                .build();
    }

    @Test
    void testRegisterRestaurant_Success() {
        when(restaurantRepository.existsByNameOrEmail(restaurantDTO.getName(), restaurantDTO.getEmail())).thenReturn(false);
        when(restaurantMapper.toEntity(restaurantDTO)).thenReturn(restaurant);
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);
        when(restaurantMapper.toDTO(restaurant)).thenReturn(restaurantDTO);

        RestaurantDTO result = restaurantService.registerRestaurant(restaurantDTO);

        assertNotNull(result);
        assertEquals(restaurantDTO.getName(), result.getName());
        verify(redisService, times(1)).initializeCapacity(eq(restaurant.getId()), eq(restaurant.getProcessingCapacity()));
    }

    @Test
    void testRegisterRestaurant_EntityAlreadyExistsException() {
        when(restaurantRepository.existsByNameOrEmail(restaurantDTO.getName(), restaurantDTO.getEmail())).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class, () -> restaurantService.registerRestaurant(restaurantDTO));

        verify(restaurantRepository, never()).save(any(Restaurant.class));
        verify(redisService, never()).initializeCapacity(anyLong(), anyDouble());
    }

    @Test
    void testUpdateRestaurant_Success() {
        Long restaurantId = 1L;
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(restaurantMapper.updateFromDTO(any(Restaurant.class), eq(restaurantDTO))).thenReturn(restaurant);
        when(restaurantRepository.save(restaurant)).thenReturn(restaurant);
        when(restaurantMapper.toDTO(restaurant)).thenReturn(restaurantDTO);

        RestaurantDTO result = restaurantService.updateRestaurant(restaurantId, restaurantDTO);

        assertNotNull(result);
        assertEquals(restaurantDTO.getName(), result.getName());
    }

    @Test
    void testUpdateRestaurant_EntityNotFoundException() {
        Long restaurantId = 1L;
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> restaurantService.updateRestaurant(restaurantId, restaurantDTO));

        verify(restaurantRepository, never()).save(any(Restaurant.class));
    }

    @Test
    void testDispatchItems_Success() {
        Long restaurantId = 1L;
        List<DispatchUpdateDTO> dispatchUpdates = List.of(
                new DispatchUpdateDTO("orderId1", "item1", 10.0),
                new DispatchUpdateDTO("orderId2", "item2", 15.0)
        );

        restaurantService.dispatchItems(restaurantId, dispatchUpdates);

        verify(redisService, times(2)).releaseCapacity(eq(restaurantId), anyDouble());
        verify(redisService).releaseCapacity(eq(restaurantId), eq(10.0));
        verify(redisService).releaseCapacity(eq(restaurantId), eq(15.0));
    }
}
