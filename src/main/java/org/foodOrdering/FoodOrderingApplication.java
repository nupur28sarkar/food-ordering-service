package org.foodOrdering;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableCaching
@EnableTransactionManagement
@EnableRedisRepositories
@ComponentScan(basePackages ="org.foodOrdering")
public class FoodOrderingApplication {
    public static void main(String[] args) {
        SpringApplication.run(FoodOrderingApplication.class, args);
    }
}