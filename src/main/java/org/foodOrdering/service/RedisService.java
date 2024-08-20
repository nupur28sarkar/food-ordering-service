package org.foodOrdering.service;

import lombok.RequiredArgsConstructor;
import org.foodOrdering.exception.EntityNotFoundException;
import org.foodOrdering.model.Restaurant;
import org.foodOrdering.repositories.RestaurantRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    private final RestaurantRepository restaurantRepository;
    private static final String CAPACITY_KEY_PREFIX = "restaurant_capacity_";

    public boolean reserveCapacity(Long restaurantId, Double quantity) {
        String key = CAPACITY_KEY_PREFIX + restaurantId;
        int retryCount = 0;
        final int maxRetries = 3; // Maximum number of retries

        while (retryCount < maxRetries) {
            try {
                // Watch the key
                redisTemplate.watch(key);
                ValueOperations<String, String> ops = redisTemplate.opsForValue();
                String currentValue = ops.get(key);

                if (currentValue == null) {
                    return false; // No capacity set for the restaurant
                }

                double currentCapacity = Double.parseDouble(currentValue);

                if (currentCapacity >= quantity) {
                    // Start transaction
                    redisTemplate.multi();
                    // Calculate the new capacity
                    double newCapacity = currentCapacity - quantity;
                    // Set the new capacity
                    ops.set(key, String.valueOf(newCapacity));
                    // Execute transaction
                    if (redisTemplate.exec() != null) {
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
            } catch (Exception e) {
                // Handle exceptions as needed
                e.printStackTrace();
            } finally {
                // Unwatch the key
                redisTemplate.unwatch();
            }
            retryCount++;
        }

        return false; // Failed to reserve capacity after retries
    }

    public void releaseCapacity(Long restaurantId, Double quantity) {
        String key = CAPACITY_KEY_PREFIX + restaurantId;
        // Increment capacity by using string conversion
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String currentValue = ops.get(key);

        double currentCapacity = currentValue != null ? Double.parseDouble(currentValue) : 0.0;
        double newCapacity = currentCapacity + quantity;
        ops.set(key, String.valueOf(newCapacity));
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
}

