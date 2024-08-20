package org.foodOrdering.dtos;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDTO {
    private Long id;
    @Nonnull
    private String name;
    @Nonnull
    private String address;
    @Nonnull
    private String email;
    @Nonnull
    private String phone;
    @Nonnull
    private int processingCapacity;
}
