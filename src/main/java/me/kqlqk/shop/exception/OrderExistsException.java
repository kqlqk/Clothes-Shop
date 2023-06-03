package me.kqlqk.shop.exception;

public class OrderExistsException extends RuntimeException {
    public OrderExistsException(String message) {
        super(message);
    }
}
