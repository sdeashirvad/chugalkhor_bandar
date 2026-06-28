package com.chugalkhorbandar.application.conversation;

public class ConversationNotFoundException extends RuntimeException {

    public ConversationNotFoundException() {
        super("No active conversation for this session.");
    }
}
