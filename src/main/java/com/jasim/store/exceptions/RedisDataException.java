package com.jasim.store.exceptions;

public class RedisDataException extends RuntimeException {
    public RedisDataException(String message) {
        super(message);
    }
}
