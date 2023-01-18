package io.vom.exceptions;

public class InfinityLoopException extends RuntimeException {
    public InfinityLoopException(String message) {
        super(message);
    }
}
