package org.foodOrdering.repositories;

import org.foodOrdering.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    boolean existsByNameOrEmail(String name, String email);
}
