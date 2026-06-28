package com.chugalkhorbandar.domain.world.ports.query;

public record StoryQuery(String participantId, String linkedStoryId) {

    public static StoryQuery all() {
        return new StoryQuery(null, null);
    }

    public static StoryQuery involvingParticipant(String participantId) {
        return new StoryQuery(participantId, null);
    }

    public static StoryQuery linkedTo(String linkedStoryId) {
        return new StoryQuery(null, linkedStoryId);
    }
}
