package io.testomat.junit5.exception;

public class TestomatException extends RuntimeException {
    public TestomatException(String message,  Throwable cause) {
        super(message);
    }
}
