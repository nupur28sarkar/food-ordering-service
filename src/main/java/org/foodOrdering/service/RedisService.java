package org.foodOrdering.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String CAPACITY_KEY_PREFIX = "restaurant_capacity_";

    public boolean reserveCapacity(Long restaurantId, Double quantity) {
        String key = CAPACITY_KEY_PREFIX + restaurantId;
        // Attempt to reserve capacity by setting a value with an expiration time.
        String result = redisTemplate.opsForValue().get(key);
        if (result != null) {
            // If the value exists, check if it can accommodate the new request.
            double currentCapacity = Double.parseDouble(result);
            if (currentCapacity >= quantity) {
                redisTemplate.opsForValue().increment(key, -quantity);
                return true;
            } else {
                return false;
            }
        } else {
            // If the key does not exist, create it with the initial value.
            redisTemplate.opsForValue().set(key, String.valueOf(-quantity), 1, TimeUnit.DAYS);
            return true;
        }
    }

    public void releaseCapacity(Long restaurantId, Double quantity) {
        String key = CAPACITY_KEY_PREFIX + restaurantId;
        redisTemplate.opsForValue().increment(key, quantity);
    }

    public Double getCapacity(Long restaurantId) {
        String key = CAPACITY_KEY_PREFIX + restaurantId;
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? Double.parseDouble(value) : 0.0;
    }
}
