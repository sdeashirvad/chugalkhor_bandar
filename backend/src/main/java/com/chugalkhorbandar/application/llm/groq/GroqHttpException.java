package com.chugalkhorbandar.application.llm.groq;

public class GroqHttpException extends Exception {

    private final int statusCode;
    private final boolean retryable;

    public GroqHttpException(String message, int statusCode, boolean retryable) {
        super(message);
        this.statusCode = statusCode;
        this.retryable = retryable;
    }

    public GroqHttpException(String message, int statusCode, boolean retryable, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.retryable = retryable;
    }

    public int statusCode() {
        return statusCode;
    }

    public boolean retryable() {
        return retryable;
    }
}
