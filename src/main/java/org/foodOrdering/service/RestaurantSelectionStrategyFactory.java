package org.foodOrdering.service;

import lombok.RequiredArgsConstructor;
import org.foodOrdering.service.impl.HigherRatingStrategyImpl;
import org.foodOrdering.service.impl.LowerCostStrategyImpl;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestaurantSelectionStrategyFactory {

    private final RedisService redisService;
    public RestaurantSelectionStrategy getStrategy(String strategyName) {
        return switch (strategyName.toLowerCase()) {
            case "price" -> new LowerCostStrategyImpl(redisService);
            case "rating" -> new HigherRatingStrategyImpl();
            default -> throw new IllegalArgumentException("Unknown strategy type: " + strategyName);
        };
    }
}
