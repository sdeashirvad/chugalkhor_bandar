package com.chugalkhorbandar.application.conversation.director;

final class ConversationDeterministicHash {

    private ConversationDeterministicHash() {}

    static int hash(String sessionId, String userMessage, String salt) {
        String combined = (sessionId == null ? "" : sessionId)
                + "|"
                + (userMessage == null ? "" : userMessage)
                + "|"
                + (salt == null ? "" : salt);
        return Math.floorMod(combined.hashCode(), Integer.MAX_VALUE);
    }
}
