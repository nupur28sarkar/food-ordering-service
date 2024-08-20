package org.foodOrdering.service;

import org.foodOrdering.dtos.OrderRequestDTO;
import org.foodOrdering.dtos.OrderResponseDTO;
import org.foodOrdering.exception.EntityNotFoundException;
import org.foodOrdering.model.Order;
import org.foodOrdering.model.OrderItem;
import org.foodOrdering.model.RestaurantMenuItem;
import org.foodOrdering.model.User;
import org.foodOrdering.model.UserSettings;
import org.foodOrdering.repositories.OrderItemRepository;
import org.foodOrdering.repositories.OrderRepository;
import org.foodOrdering.repositories.RestaurantMenuItemRepository;
import org.foodOrdering.repositories.RestaurantRepository;
import org.foodOrdering.repositories.UserRepository;
import org.foodOrdering.repositories.UserSettingsRepository;
import org.foodOrdering.service.RestaurantSelectionContext;
import org.foodOrdering.service.RestaurantSelectionStrategy;
import org.foodOrdering.service.RestaurantSelectionStrategyFactory;
import org.foodOrdering.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantMenuItemRepository restaurantMenuItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private UserSettingsRepository userSettingsRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestaurantSelectionStrategyFactory restaurantSelectionStrategyFactory;

    @InjectMocks
    private OrderServiceImpl orderService;

    public OrderServiceImplTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void placeOrder_Success() {
        User user = new User();
        OrderRequestDTO requestDTO = new OrderRequestDTO();
        requestDTO.setItemId(1L);
        RestaurantMenuItem menuItem = new RestaurantMenuItem();
        menuItem.getMenuItem().setId(1L);
        Order order = Order.builder().build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(restaurantMenuItemRepository.findAllByMenuItemIdIn(anyList())).thenReturn(List.of(menuItem));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(restaurantSelectionStrategyFactory.getStrategy(anyString())).thenReturn(mock(RestaurantSelectionStrategy.class));

        OrderResponseDTO result = orderService.placeOrder(1L, List.of(requestDTO));

        assertNotNull(result);
        verify(orderItemRepository, times(1)).saveAll(anyList());
    }

    @Test
    void placeOrder_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            orderService.placeOrder(1L, List.of(new OrderRequestDTO()));
        });
    }

    @Test
    void placeOrder_InsufficientItems() {
        User user = new User();
        OrderRequestDTO requestDTO = new OrderRequestDTO();
        requestDTO.setItemId(1L);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(restaurantMenuItemRepository.findAllByMenuItemIdIn(anyList())).thenReturn(List.of());

        assertThrows(RuntimeException.class, () -> {
            orderService.placeOrder(1L, List.of(requestDTO));
        });
    }
}
