package ru.service.router.exception;

public class InternalServerException extends RuntimeException {
    public InternalServerException(String msg) {
        super(msg);
    }
}
