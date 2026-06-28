package com.chugalkhorbandar.application.conversation.director;

import java.util.ArrayList;
import java.util.List;

public final class ConversationDelayDeriver {

    private ConversationDelayDeriver() {}

    public static List<Long> derive(
            int messageCount, String sessionId, String userMessage, ConversationDirectorProperties properties) {
        if (messageCount <= 1) {
            return List.of();
        }
        int hash = ConversationDeterministicHash.hash(sessionId, userMessage, "delays");
        List<Long> delays = new ArrayList<>();
        ConversationDirectorProperties.MessageDelayRange range = properties.getMessageDelayRange();
        if (messageCount >= 2) {
            delays.add(effectiveDelay(
                    pickInRange(hash, range.getSecondMessageMinMs(), range.getSecondMessageMaxMs()), properties));
        }
        if (messageCount >= 3) {
            delays.add(effectiveDelay(
                    pickInRange(hash / 31, range.getThirdMessageMinMs(), range.getThirdMessageMaxMs()), properties));
        }
        return List.copyOf(delays);
    }

    private static long pickInRange(int hash, long minMs, long maxMs) {
        if (maxMs <= minMs) {
            return minMs;
        }
        long span = maxMs - minMs;
        return minMs + Math.floorMod(hash, (int) span + 1);
    }

    private static long effectiveDelay(long baseMs, ConversationDirectorProperties properties) {
        return Math.max(0L, Math.round(baseMs * properties.getDevDelayMultiplier()));
    }
}
