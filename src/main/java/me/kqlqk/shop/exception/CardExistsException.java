package me.kqlqk.shop.exception;

public class CardExistsException extends RuntimeException {
    public CardExistsException(String message) {
        super(message);
    }
}
