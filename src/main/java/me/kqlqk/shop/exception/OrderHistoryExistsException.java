package me.kqlqk.shop.exception;

public class OrderHistoryExistsException extends RuntimeException {
    public OrderHistoryExistsException(String message) {
        super(message);
    }
}
