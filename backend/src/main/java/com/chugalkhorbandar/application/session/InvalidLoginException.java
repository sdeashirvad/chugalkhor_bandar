package com.chugalkhorbandar.application.session;

public final class InvalidLoginException extends RuntimeException {

    public InvalidLoginException() {
        super("Invalid animal name or passkey");
    }
}
