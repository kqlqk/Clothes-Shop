package me.kqlqk.shop.exception;

public class OrderHistoryNotFoundException extends RuntimeException {
    public OrderHistoryNotFoundException(String message) {
        super(message);
    }
}
