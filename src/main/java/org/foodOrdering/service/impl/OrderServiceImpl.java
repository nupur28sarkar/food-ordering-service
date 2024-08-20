package org.foodOrdering.service.impl;

import lombok.RequiredArgsConstructor;
import org.foodOrdering.dtos.OrderRequestDTO;
import org.foodOrdering.dtos.RestaurantOrderItem;
import org.foodOrdering.enums.SelectionStrategy;
import org.foodOrdering.exception.EntityNotFoundException;
import org.foodOrdering.model.*;
import org.foodOrdering.repositories.*;
import org.foodOrdering.service.OrderService;
import org.foodOrdering.service.RedisService;
import org.foodOrdering.service.RestaurantSelectionContext;
import org.foodOrdering.service.RestaurantSelectionStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    @Autowired
    private final RestaurantRepository restaurantRepository;

    @Autowired
    private final RestaurantMenuItemRepository restaurantMenuItemRepository;

    @Autowired
    private final OrderRepository orderRepository;

    @Autowired
    private final OrderItemRepository orderItemRepository;

    @Autowired
    private final RedisService redisService;

    @Autowired
    private final UserSettingsRepository userSettingsRepository;

    @Autowired
    private final UserRepository userRepository;

    @Override
    public void placeOrder(Long userId, List<OrderRequestDTO> orderRequestDTOList) {
        List<Long> menuItemIds = orderRequestDTOList.stream().map(OrderRequestDTO::getItemId).collect(Collectors.toList());
        List<RestaurantMenuItem> availableItems = restaurantMenuItemRepository.findAllByMenuItemIn(menuItemIds);

        if (availableItems.isEmpty()) {
            throw new RuntimeException("No restaurants can fulfill the order.");
        }
        Optional<UserSettings> userSettingsRepositoryOptional = userSettingsRepository.findByUserId(userId);
        RestaurantSelectionStrategy strategy = null;
        if (userSettingsRepositoryOptional.isPresent()) {
            strategy = getStrategy(userSettingsRepositoryOptional.get().getSelectionStrategy().name());
        }
        //if absent use min price as default strategy
            RestaurantSelectionContext context = new RestaurantSelectionContext(strategy);
            List<RestaurantOrderItem> selectedItems = new ArrayList<>();
            // Process each item in the order
            for (OrderRequestDTO request : orderRequestDTOList) {
                List<RestaurantMenuItem> restaurants = availableItems.stream()
                        .filter(item -> item.getMenuItem().getId().equals(request.getItemId()))
                        .collect(Collectors.toList());

                if (restaurants.isEmpty()) {
                    throw new RuntimeException("No restaurant can fulfill item: " + request.getItemId());
                }

                // Use strategy to select restaurant

                selectedItems.addAll(context.executeStrategy(restaurants, request.getQuantity()));

            }
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()) {
            throw new EntityNotFoundException("User with userid" + userId + "is not present");
        }
        Order order = Order.builder().user(userOptional.get()).build();
        orderRepository.save(order);
        List<OrderItem> orderItemList = new ArrayList<>();
        for (RestaurantOrderItem selectedItem : selectedItems) {
            orderItemList.add(OrderItem.builder().menuItem(selectedItem.getMenuItem()).quantity(selectedItem.getQuantity())
                            .order(order).build());
        }
        orderItemRepository.saveAll(orderItemList);
        }

    }
    private RestaurantSelectionStrategy getStrategy(String strategyName) {
    if(SelectionStrategy.PRICE.name().equalsIgnoreCase(strategyName)) {
        return new LowerCostStrategyImpl();
    }
    return new HigherRatingStrategyImpl();
}
}

