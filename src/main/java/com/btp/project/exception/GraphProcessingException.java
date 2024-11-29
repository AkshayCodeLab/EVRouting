package com.btp.project.exception;

public class GraphProcessingException extends RuntimeException {
    public GraphProcessingException(String message) {
        super(message);
    }

    public GraphProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
