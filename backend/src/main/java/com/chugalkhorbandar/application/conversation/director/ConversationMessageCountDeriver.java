package com.chugalkhorbandar.application.conversation.director;

public final class ConversationMessageCountDeriver {

    private ConversationMessageCountDeriver() {}

    public static int derive(
            ConversationEnergy energy,
            String sessionId,
            String userMessage,
            ConversationDirectorProperties properties) {
        int hash = ConversationDeterministicHash.hash(sessionId, userMessage, "message-count");
        int maxMessages = Math.max(1, properties.getMaxMessages());
        return switch (energy) {
            case LOW -> 1;
            case MEDIUM -> hash % properties.getConversationEnergyThresholds().getMediumSecondMessageModulo()
                            == 0
                    ? Math.min(2, maxMessages)
                    : 1;
            case HIGH -> hash % properties.getConversationEnergyThresholds().getHighThirdMessageModulo() == 0
                    ? Math.min(3, maxMessages)
                    : Math.min(2, maxMessages);
            case VERY_HIGH -> Math.min(3, maxMessages);
        };
    }
}
