package com.chugalkhorbandar.application.memory.consolidation;

public record MemoryConsolidationDailyStats(
        String date,
        int conversations,
        int artifacts,
        int inboxItems,
        int promoted,
        int discarded,
        int candidates,
        int pendingPromises,
        int unreadNotifications) {}
