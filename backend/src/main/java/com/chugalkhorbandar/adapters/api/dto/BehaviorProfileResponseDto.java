package com.chugalkhorbandar.adapters.api.dto;

import com.chugalkhorbandar.application.behavior.ConversationFlavor;
import com.chugalkhorbandar.application.behavior.CuriosityLevel;
import com.chugalkhorbandar.application.behavior.EnergyModifier;
import com.chugalkhorbandar.application.behavior.EndingStyle;
import com.chugalkhorbandar.application.behavior.HumorLevel;
import com.chugalkhorbandar.application.behavior.NarrationStyle;
import com.chugalkhorbandar.application.behavior.OpeningStyle;
import com.chugalkhorbandar.application.behavior.StorytellingPreference;
import java.time.Instant;
import java.util.List;

public record BehaviorProfileResponseDto(
        String sessionId,
        OpeningStyle openingStyle,
        NarrationStyle narrationStyle,
        HumorLevel humorLevel,
        CuriosityLevel curiosityLevel,
        EndingStyle endingStyle,
        ConversationFlavor conversationFlavor,
        EnergyModifier energyModifier,
        StorytellingPreference storytellingPreference,
        Instant createdAt,
        List<BehaviorPlanningTraceEntryDto> trace) {}
