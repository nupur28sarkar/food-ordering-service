package org.foodOrdering.service;

import org.foodOrdering.dtos.RestaurantMenuItemDTO;
import org.foodOrdering.exception.EntityNotFoundException;
import org.foodOrdering.mapper.RestaurantMenuItemMapper;
import org.foodOrdering.model.Restaurant;
import org.foodOrdering.model.RestaurantMenuItem;
import org.foodOrdering.repositories.RestaurantMenuItemRepository;
import org.foodOrdering.repositories.RestaurantRepository;
import org.foodOrdering.service.impl.RestaurantMenuServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestaurantMenuServiceImplTest {

    @Mock
    private RestaurantMenuItemRepository restaurantMenuItemRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantMenuItemMapper restaurantMenuItemMapper;

    @InjectMocks
    private RestaurantMenuServiceImpl restaurantMenuService;

    public RestaurantMenuServiceImplTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addMenuItem_Success() {
        Long restaurantId = 1L;
        RestaurantMenuItemDTO dto = new RestaurantMenuItemDTO();
        Restaurant restaurant = new Restaurant();

        when(restaurantRepository.findById(anyLong())).thenReturn(Optional.of(restaurant));
        when(restaurantMenuItemMapper.toEntity(any(RestaurantMenuItemDTO.class), anyLong())).thenReturn(new RestaurantMenuItem());
        when(restaurantMenuItemRepository.saveAll(anyList())).thenReturn(List.of(new RestaurantMenuItem()));

        List<RestaurantMenuItem> result = restaurantMenuService.addMenuItem(restaurantId, List.of(dto));

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void addMenuItem_RestaurantNotFound() {
        when(restaurantRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            restaurantMenuService.addMenuItem(1L, List.of(new RestaurantMenuItemDTO()));
        });

        verify(restaurantMenuItemRepository, never()).saveAll(anyList());
    }
}
