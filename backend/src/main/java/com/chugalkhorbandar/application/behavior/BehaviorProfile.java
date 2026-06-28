package com.chugalkhorbandar.application.behavior;

import java.time.Instant;

public record BehaviorProfile(
        OpeningStyle openingStyle,
        NarrationStyle narrationStyle,
        HumorLevel humorLevel,
        CuriosityLevel curiosityLevel,
        EndingStyle endingStyle,
        ConversationFlavor conversationFlavor,
        EnergyModifier energyModifier,
        StorytellingPreference storytellingPreference,
        Instant createdAt) {}
