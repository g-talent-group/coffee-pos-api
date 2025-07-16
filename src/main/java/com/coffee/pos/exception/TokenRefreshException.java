package com.coffee.pos.exception;

public class TokenRefreshException extends RuntimeException {

    public TokenRefreshException(String token, String s) {
        super(s);
    }
}
