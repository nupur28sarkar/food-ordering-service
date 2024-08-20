package org.foodOrdering.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.foodOrdering.model.RestaurantMenuItem;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantOrderItem {
    private RestaurantMenuItem menuItem;
    private Double quantity;
}
