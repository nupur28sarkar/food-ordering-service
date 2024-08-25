package org.foodOrdering.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDTO {

    private Long id;

    private String name;

    private String address;

    private String email;

    private String phone;

    private double processingCapacity;
}
