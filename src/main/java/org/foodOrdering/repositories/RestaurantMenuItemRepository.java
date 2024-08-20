package org.foodOrdering.repositories;

import org.foodOrdering.model.RestaurantMenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantMenuItemRepository extends JpaRepository<RestaurantMenuItem, Long> {

    List<RestaurantMenuItem> findAllByRestaurantId(Long restaurantId);

    List<RestaurantMenuItem> findAllByMenuItemIn(java.util.List<java.lang.Long> menuItemIds);
}
