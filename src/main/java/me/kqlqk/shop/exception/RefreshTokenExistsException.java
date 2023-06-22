package me.kqlqk.shop.exception;

public class RefreshTokenExistsException extends RuntimeException {
    public RefreshTokenExistsException(String message) {
        super(message);
    }
}
