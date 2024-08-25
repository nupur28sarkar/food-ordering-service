package org.foodOrdering.service.impl;

import lombok.RequiredArgsConstructor;
import org.foodOrdering.dtos.DispatchUpdateDTO;
import org.foodOrdering.dtos.RestaurantDTO;
import org.foodOrdering.exception.EntityAlreadyExistsException;
import org.foodOrdering.exception.EntityNotFoundException;
import org.foodOrdering.mapper.RestaurantMapper;
import org.foodOrdering.model.Restaurant;
import org.foodOrdering.repositories.RestaurantRepository;
import org.foodOrdering.service.RedisService;
import org.foodOrdering.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;

    private final RestaurantMapper restaurantMapper;

    private final RedisService redisService;

    @Override
    public RestaurantDTO registerRestaurant(RestaurantDTO restaurantDTO) {
        if (restaurantRepository.existsByNameOrEmail(restaurantDTO.getName(), restaurantDTO.getEmail())) {
            throw new EntityAlreadyExistsException("Restaurant with this name or email already exists");
        }
        Restaurant restaurant = restaurantRepository.save(restaurantMapper.toEntity(restaurantDTO));
        redisService.initializeCapacity(restaurant.getId(), restaurant.getProcessingCapacity());
        return restaurantMapper.toDTO(restaurant);
    }

    @Override
    public RestaurantDTO updateRestaurant(Long id, RestaurantDTO updatedRestaurant) {
        Optional<Restaurant> restaurantOptional = restaurantRepository.findById(id);
        if(restaurantOptional.isEmpty()) {
            throw new EntityNotFoundException("Restaurant with given id does not exist");
        }
        Restaurant restaurant = restaurantMapper.updateFromDTO(restaurantOptional.get(), updatedRestaurant);
        restaurantRepository.save(restaurant);
        return restaurantMapper.toDTO(restaurant);
    }

    @Override
    public void dispatchItems(Long restaurantId, List<DispatchUpdateDTO> dispatchRequests) {
        for (DispatchUpdateDTO dispatchRequest : dispatchRequests) {
            redisService.releaseCapacity(restaurantId, dispatchRequest.getQuantity());
        }
    }


}
