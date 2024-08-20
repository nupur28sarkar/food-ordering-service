package org.foodOrdering.service.impl;

import lombok.RequiredArgsConstructor;
import org.foodOrdering.dtos.OrderRequestDTO;
import org.foodOrdering.dtos.OrderResponseDTO;
import org.foodOrdering.dtos.RestaurantOrderItem;
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
import org.foodOrdering.service.OrderService;
import org.foodOrdering.service.RestaurantSelectionContext;
import org.foodOrdering.service.RestaurantSelectionStrategy;
import org.foodOrdering.service.RestaurantSelectionStrategyFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final RestaurantRepository restaurantRepository;

    private final RestaurantMenuItemRepository restaurantMenuItemRepository;

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final UserSettingsRepository userSettingsRepository;

    private final UserRepository userRepository;

    private final RestaurantSelectionStrategyFactory restaurantSelectionStrategyFactory;

    @Override
    public OrderResponseDTO placeOrder(Long userId, List<OrderRequestDTO> orderRequestDTOList) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User with userid" + userId + "is not present");
        }
        List<Long> menuItemIds = orderRequestDTOList.stream().map(OrderRequestDTO::getItemId).collect(Collectors.toList());
        List<RestaurantMenuItem> availableItems = restaurantMenuItemRepository.findAllByMenuItemIdIn(menuItemIds);

        if (availableItems.size() < orderRequestDTOList.size()) {
            throw new RuntimeException("Order cannot be fulfilled");
        }
        Optional<UserSettings> userSettingsRepositoryOptional = userSettingsRepository.findByUserId(userId);
        RestaurantSelectionStrategy strategy = null;
        if (userSettingsRepositoryOptional.isPresent()) {
            strategy = restaurantSelectionStrategyFactory.getStrategy(userSettingsRepositoryOptional.get().getSelectionStrategy().name());
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

            selectedItems.addAll(context.executeStrategy(restaurants, request.getQuantity()));

        }
        Order order = Order.builder().user(userOptional.get()).build();
        Order createdOrder = orderRepository.save(order);
        List<OrderItem> orderItemList = new ArrayList<>();
        for (RestaurantOrderItem selectedItem : selectedItems) {
            orderItemList.add(OrderItem.builder().menuItem(selectedItem.getMenuItem()).quantity(selectedItem.getQuantity())
                    .order(order).build());
        }
        orderItemRepository.saveAll(orderItemList);
        return OrderResponseDTO.builder().orderId(createdOrder.getId()).build();
    }
}

