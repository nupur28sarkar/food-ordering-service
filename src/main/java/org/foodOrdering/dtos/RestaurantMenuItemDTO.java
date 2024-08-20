package org.foodOrdering.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantMenuItemDTO {

    private Long id;

    private Long itemId;

    private double price;

}
