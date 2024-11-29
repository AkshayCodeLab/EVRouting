package com.btp.project.graph.exception;

public class GraphConstructionException extends RuntimeException {
    public GraphConstructionException(String message) {
        super(message);
    }

    public GraphConstructionException(String message, Throwable cause) {
        super(message, cause);
    }
}
