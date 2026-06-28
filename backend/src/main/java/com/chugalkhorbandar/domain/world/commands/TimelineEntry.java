package com.chugalkhorbandar.domain.world.commands;

public record TimelineEntry(
        String era, String approximateDate, String event, String description, String linkedStory) {}
