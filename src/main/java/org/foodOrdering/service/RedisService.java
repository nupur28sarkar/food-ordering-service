package org.foodOrdering.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.foodOrdering.dtos.MenuItemDTO;
import org.foodOrdering.exception.EntityNotFoundException;
import org.foodOrdering.model.MenuItem;
import org.foodOrdering.model.Restaurant;
import org.foodOrdering.repositories.RestaurantRepository;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    private final RestaurantRepository restaurantRepository;

    private final ObjectMapper objectMapper;
    private static final String CAPACITY_KEY_PREFIX = "restaurant_capacity_";

    private static final String MENU_ITEMS_CACHE_KEY = "menu_items_cache";

    public boolean reserveCapacity(Long restaurantId, Double quantity) {
        String key = CAPACITY_KEY_PREFIX + restaurantId;
        int retryCount = 0;
        final int maxRetries = 3; // Maximum number of retries

        while (retryCount < maxRetries) {
            try {
                // Use SessionCallback to handle transactions
                Boolean result = redisTemplate.execute(new SessionCallback<Boolean>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public Boolean execute(RedisOperations operations) {
                        operations.watch(key); // Watch the key

                        ValueOperations<String, String> ops = operations.opsForValue();
                        String currentValue = ops.get(key);

                        if (currentValue == null) {
                            operations.unwatch();
                            return false; // No capacity set for the restaurant
                        }

                        double currentCapacity = Double.parseDouble(currentValue);

                        if (currentCapacity >= quantity) {
                            operations.multi(); // Start transaction
                            double newCapacity = currentCapacity - quantity;
                            ops.set(key, String.valueOf(newCapacity)); // Queue the update command

                            // Execute the transaction
                            List<Object> execResult = operations.exec();
                            if (execResult != null && !execResult.isEmpty()) {
                                // Transaction was successful
                                String lockKey = "lock:restaurant_capacity_" + restaurantId;
                                Boolean acquired = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", 10, TimeUnit.SECONDS);
                                if (Boolean.TRUE.equals(acquired)) {
                                    try {
                                        updateDatabaseCapacityAsync(restaurantId, newCapacity);
                                    } finally {
                                        redisTemplate.delete(lockKey);
                                    }
                                }
                                return true; // Reservation successful
                            }
                        }
                        return false; // Transaction failed or not enough capacity
                    }
                });

                if (result != null && result) {
                    return true; // Reservation successful
                }
            } catch (Exception e) {
                // Handle exceptions as needed
                e.printStackTrace();
            }
            retryCount++;
        }

        return false;
    }

    public void releaseCapacity(Long restaurantId, Double quantity) {
        String key = CAPACITY_KEY_PREFIX + restaurantId;
        // Increment capacity by using string conversion
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String currentValue = ops.get(key);

        double currentCapacity = currentValue != null ? Double.parseDouble(currentValue) : 0.0;
        double newCapacity = currentCapacity + quantity;
        ops.set(key, String.valueOf(newCapacity));
        updateDatabaseCapacityAsync(restaurantId, newCapacity);
    }

    public Double getCapacity(Long restaurantId) {
        String key = CAPACITY_KEY_PREFIX + restaurantId;
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? Double.parseDouble(value) : 0.0;
    }

    public void initializeCapacity(Long restaurantId, double capacity) {
        String key = CAPACITY_KEY_PREFIX + restaurantId;
        redisTemplate.opsForValue().set(key, String.valueOf(capacity), 1, TimeUnit.DAYS);
    }

    private void updateDatabaseCapacityAsync(Long restaurantId, double newCapacity) {
        CompletableFuture.runAsync(() -> {
            try {
                Restaurant restaurant = restaurantRepository.findById(restaurantId)
                        .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));
                restaurant.setProcessingCapacity(newCapacity);
                restaurantRepository.save(restaurant);
            } catch (Exception e) {
                // Log and handle exceptions
                e.printStackTrace();
            }
        });
    }

    public void updateMenuCache(List<MenuItemDTO> menuItemList) {
        List<MenuItemDTO> currentCache = getMenuItemsFromCache();

        if (currentCache == null) {
            // If no cache, simply add all items
            currentCache = new ArrayList<>();
        }

        // Add new items to the cache, avoiding duplicates
        Set<MenuItemDTO> updatedCache = new HashSet<>(currentCache);
        updatedCache.addAll(menuItemList);

        // Serialize the updated list and store it in Redis
        try {
            String json = objectMapper.writeValueAsString(new ArrayList<>(updatedCache));
            redisTemplate.opsForValue().set(MENU_ITEMS_CACHE_KEY, json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public List<MenuItemDTO> getMenuItemsFromCache() {
        String json = redisTemplate.opsForValue().get(MENU_ITEMS_CACHE_KEY);
        if (json != null) {
            try {
                return objectMapper.readValue(json, new TypeReference<List<MenuItemDTO>>() {});
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

