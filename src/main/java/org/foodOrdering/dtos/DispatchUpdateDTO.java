package org.foodOrdering.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DispatchUpdateDTO {
    private String orderId;
    private String item;
    private Double quantity;
}
