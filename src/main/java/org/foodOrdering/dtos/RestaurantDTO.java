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

    @NotBlank(message = "Name cannot be null or empty")
    private String name;

    @NotBlank(message = "Address cannot be null or empty")
    private String address;

    @NotBlank(message = "Email cannot be null or empty")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Phone cannot be null or empty")
    private String phone;

    @Positive(message = "Processing capacity must be positive")
    private double processingCapacity;
}
