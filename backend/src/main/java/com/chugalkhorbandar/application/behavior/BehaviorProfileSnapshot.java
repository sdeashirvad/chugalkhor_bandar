package com.chugalkhorbandar.application.behavior;

public record BehaviorProfileSnapshot(
        String sessionId, BehaviorProfile profile, BehaviorPlanningTrace trace) {}
