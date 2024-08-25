package org.foodOrdering.exception;

public class OrderNotFulfilledException extends RuntimeException {
    public OrderNotFulfilledException(String message) {
        super(message);
    }
}
