package com.chugalkhorbandar.application.behavior;

public final class BehaviorInstructionBuilder {

    private BehaviorInstructionBuilder() {}

    public static String build(BehaviorProfile profile) {
        if (profile == null) {
            return "";
        }
        StringBuilder content = new StringBuilder();
        content.append(flavorLine(profile.conversationFlavor(), profile.energyModifier()));
        content.append('\n').append(openingLine(profile.openingStyle()));
        content.append('\n').append(narrationLine(profile.narrationStyle(), profile.storytellingPreference()));
        content.append('\n').append(humorLine(profile.humorLevel()));
        if (profile.curiosityLevel() == CuriosityLevel.HIGH || profile.curiosityLevel() == CuriosityLevel.MEDIUM) {
            content.append('\n').append(curiosityLine(profile.curiosityLevel()));
        }
        content.append('\n').append(endingLine(profile.endingStyle()));
        return content.toString().trim();
    }

    private static String flavorLine(ConversationFlavor flavor, EnergyModifier energy) {
        String feel = switch (flavor) {
            case COZY -> "cozy and warm";
            case CURIOUS -> "calm and curious";
            case NOSTALGIC -> "reflective and nostalgic";
            case ADVENTUROUS -> "bright and adventurous";
            case CALM -> "calm and steady";
            case CELEBRATORY -> "festive and celebratory";
            case MYSTERIOUS -> "gentle and mysterious";
        };
        if (energy == EnergyModifier.LIVELY) {
            feel = feel + ", with lively energy";
        } else if (energy == EnergyModifier.SUBDUED) {
            feel = feel + ", with quiet restraint";
        }
        return "Today you feel " + feel + ".";
    }

    private static String openingLine(OpeningStyle opening) {
        return switch (opening) {
            case GREETING -> "Open with a warm greeting.";
            case OBSERVATION -> "Open with a gentle observation.";
            case QUESTION -> "Open with a thoughtful question.";
            case MEMORY -> "Open by recalling a fitting memory or scene.";
            case JOKE -> "Open with a gentle, good-natured remark.";
            case DIRECT -> "Open directly and clearly.";
        };
    }

    private static String narrationLine(NarrationStyle narration, StorytellingPreference storytelling) {
        String style = switch (narration) {
            case DIRECT -> "Speak plainly and directly.";
            case STORY -> "Prefer storytelling over exposition.";
            case ANALOGY -> "Use a simple analogy when it helps understanding.";
            case HISTORICAL -> "Prefer historical storytelling.";
            case PLAYFUL -> "Keep the tone playful and light.";
            case REFLECTIVE -> "Take a reflective, unhurried tone.";
        };
        if (storytelling == StorytellingPreference.STRONG) {
            return style + " Let the narrative unfold naturally.";
        }
        if (storytelling == StorytellingPreference.MINIMAL) {
            return style + " Keep narration concise.";
        }
        return style;
    }

    private static String humorLine(HumorLevel humor) {
        return switch (humor) {
            case OFF -> "Avoid humor in this reply.";
            case LIGHT -> "Use light humor only if it feels natural.";
            case MEDIUM -> "A gentle touch of humor is welcome, never sharp or sarcastic.";
        };
    }

    private static String curiosityLine(CuriosityLevel curiosity) {
        return switch (curiosity) {
            case LOW -> "Do not press the conversation forward with extra questions.";
            case MEDIUM -> "You may show moderate curiosity when it fits.";
            case HIGH -> "You may show gentle curiosity through a remark or memory — not probing questions about feelings.";
        };
    }

    private static String endingLine(EndingStyle ending) {
        return switch (ending) {
            case NONE -> "End cleanly without extending the conversation.";
            case QUESTION -> "End with a reflective pause, a tease, or a gentle remark — not a counselling question.";
            case REFLECTION -> "End with a brief reflection.";
            case INVITATION -> "End with a gentle invitation to continue.";
            case PROMISE -> "End with a reassuring promise within this conversation.";
        };
    }
}
