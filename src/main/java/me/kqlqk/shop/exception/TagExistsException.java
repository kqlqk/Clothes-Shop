package me.kqlqk.shop.exception;

public class TagExistsException extends RuntimeException {
    public TagExistsException(String message) {
        super(message);
    }
}
