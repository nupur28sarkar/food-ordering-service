package org.foodOrdering.service;

import org.foodOrdering.dtos.OrderRequestDTO;
import org.foodOrdering.dtos.OrderResponseDTO;
import org.foodOrdering.dtos.RestaurantOrderItem;
import org.foodOrdering.exception.EntityNotFoundException;
import org.foodOrdering.exception.OrderNotFulfilledException;
import org.foodOrdering.model.*;
import org.foodOrdering.repositories.*;
import org.foodOrdering.service.*;
import org.foodOrdering.service.impl.OrderServiceImpl;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

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

    @Mock
    private RedisService redisService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Order order;
    private RestaurantMenuItem menuItem1;
    private RestaurantMenuItem menuItem2;
    private OrderRequestDTO orderRequestDTO1;
    private OrderRequestDTO orderRequestDTO2;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        menuItem1 = new RestaurantMenuItem();
        menuItem1.setId(1L);
        menuItem1.setMenuItem(MenuItem.builder().id(1L).build());
        menuItem1.setPrice(5.0);
        menuItem1.setRestaurant(Restaurant.builder().id(1L).build());

        menuItem2 = new RestaurantMenuItem();
        menuItem2.setId(2L);
        menuItem2.setMenuItem(MenuItem.builder().id(2L).build());
        menuItem2.setPrice(10.0);
        menuItem2.setRestaurant(Restaurant.builder().id(2L).build());

        orderRequestDTO1 = new OrderRequestDTO(1L, 2.0);
        orderRequestDTO2 = new OrderRequestDTO(2L, 3.0);

        order = Order.builder().id(1L).user(user).build();
    }

    @Test
    void testPlaceOrder_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(restaurantMenuItemRepository.findAllByMenuItemIdIn(anyList())).thenReturn(List.of(menuItem1, menuItem2));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(userSettingsRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(redisService.getCapacity(anyLong())).thenReturn(10.0);
        when(orderItemRepository.saveAll(anyList())).thenReturn(null);
        when(redisService.reserveCapacity(anyLong(), any())).thenReturn(true);

        RestaurantSelectionStrategy mockStrategy = (menuItems, quantity) -> List.of(
                new RestaurantOrderItem(menuItem1, 2.0),
                new RestaurantOrderItem(menuItem2, 3.0)
        );
//        when(restaurantSelectionStrategyFactory.getStrategy(anyString())).thenReturn(mockStrategy);

        OrderResponseDTO response = orderService.placeOrder(1L, List.of(orderRequestDTO1, orderRequestDTO2));

        assertNotNull(response);
        assertEquals(1L, response.getOrderId());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderItemRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testPlaceOrder_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> orderService.placeOrder(1L, List.of(orderRequestDTO1, orderRequestDTO2)));

        assertEquals("User with userid1is not present", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void testPlaceOrder_ItemsNotAvailable() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(restaurantMenuItemRepository.findAllByMenuItemIdIn(anyList())).thenReturn(List.of(menuItem1));

        OrderNotFulfilledException exception = assertThrows(OrderNotFulfilledException.class,
                () -> orderService.placeOrder(1L, List.of(orderRequestDTO1, orderRequestDTO2)));

        assertEquals("Order cannot be fulfilled", exception.getMessage());
        verify(restaurantMenuItemRepository, times(1)).findAllByMenuItemIdIn(anyList());
        verifyNoMoreInteractions(orderRepository);
    }

}
