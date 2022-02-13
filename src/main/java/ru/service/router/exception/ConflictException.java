package ru.service.router.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String msg) {
        super(msg);
    }
}
