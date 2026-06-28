package com.chugalkhorbandar.adapters.persistence.memory;

import com.chugalkhorbandar.domain.world.runtime.RuntimeCanon;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCharacter;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCustom;
import com.chugalkhorbandar.domain.world.runtime.RuntimeGlossaryEntry;
import com.chugalkhorbandar.domain.world.runtime.RuntimeLaw;
import com.chugalkhorbandar.domain.world.runtime.RuntimeObject;
import com.chugalkhorbandar.domain.world.runtime.RuntimeOrganization;
import com.chugalkhorbandar.domain.world.runtime.RuntimePlace;
import com.chugalkhorbandar.domain.world.runtime.RuntimePromptProfile;
import com.chugalkhorbandar.domain.world.runtime.RuntimeRelationship;
import com.chugalkhorbandar.domain.world.runtime.RuntimeResource;
import com.chugalkhorbandar.domain.world.runtime.RuntimeStory;
import com.chugalkhorbandar.domain.world.runtime.RuntimeTerritory;
import com.chugalkhorbandar.domain.world.runtime.RuntimeTimelineEntry;
import com.chugalkhorbandar.domain.world.runtime.RuntimeWorldRules;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class InMemoryWorldStore {

    private final ConcurrentHashMap<String, RuntimeCharacter> characters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, RuntimeTerritory> territories = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, RuntimePlace> places = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, RuntimeStory> stories = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, RuntimeRelationship> relationships = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, RuntimeOrganization> organizations = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, RuntimeResource> resources = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, RuntimeObject> objects = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, RuntimeTimelineEntry> timeline = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, RuntimePromptProfile> promptProfiles = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, RuntimeCanon> canon = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, RuntimeWorldRules> worldRules = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, RuntimeLaw> laws = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, RuntimeCustom> customs = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, RuntimeGlossaryEntry> glossary = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Instant> timelineRecordedAt = new ConcurrentHashMap<>();
    private final AtomicLong timelineSequence = new AtomicLong();

    public ConcurrentHashMap<String, RuntimeCharacter> characters() {
        return characters;
    }

    public ConcurrentHashMap<String, RuntimeTerritory> territories() {
        return territories;
    }

    public ConcurrentHashMap<String, RuntimePlace> places() {
        return places;
    }

    public ConcurrentHashMap<String, RuntimeStory> stories() {
        return stories;
    }

    public ConcurrentHashMap<String, RuntimeRelationship> relationships() {
        return relationships;
    }

    public ConcurrentHashMap<String, RuntimeOrganization> organizations() {
        return organizations;
    }

    public ConcurrentHashMap<String, RuntimeResource> resources() {
        return resources;
    }

    public ConcurrentHashMap<String, RuntimeObject> objects() {
        return objects;
    }

    public ConcurrentHashMap<String, RuntimeTimelineEntry> timeline() {
        return timeline;
    }

    public ConcurrentHashMap<String, RuntimePromptProfile> promptProfiles() {
        return promptProfiles;
    }

    public ConcurrentHashMap<String, RuntimeCanon> canon() {
        return canon;
    }

    public ConcurrentHashMap<String, RuntimeWorldRules> worldRules() {
        return worldRules;
    }

    public ConcurrentHashMap<String, RuntimeLaw> laws() {
        return laws;
    }

    public ConcurrentHashMap<String, RuntimeCustom> customs() {
        return customs;
    }

    public ConcurrentHashMap<String, RuntimeGlossaryEntry> glossary() {
        return glossary;
    }

    public ConcurrentHashMap<String, Instant> timelineRecordedAt() {
        return timelineRecordedAt;
    }

    public Instant nextTimelineTimestamp() {
        return Instant.ofEpochSecond(0, timelineSequence.incrementAndGet());
    }

    public Snapshot snapshot() {
        return new Snapshot(
                copy(characters),
                copy(territories),
                copy(places),
                copy(stories),
                copy(relationships),
                copy(organizations),
                copy(resources),
                copy(objects),
                copy(timeline),
                copy(promptProfiles),
                copy(canon),
                copy(worldRules),
                copy(laws),
                copy(customs),
                copy(glossary),
                copy(timelineRecordedAt),
                timelineSequence.get());
    }

    public void restore(Snapshot snapshot) {
        replace(characters, snapshot.characters);
        replace(territories, snapshot.territories);
        replace(places, snapshot.places);
        replace(stories, snapshot.stories);
        replace(relationships, snapshot.relationships);
        replace(organizations, snapshot.organizations);
        replace(resources, snapshot.resources);
        replace(objects, snapshot.objects);
        replace(timeline, snapshot.timeline);
        replace(promptProfiles, snapshot.promptProfiles);
        replace(canon, snapshot.canon);
        replace(worldRules, snapshot.worldRules);
        replace(laws, snapshot.laws);
        replace(customs, snapshot.customs);
        replace(glossary, snapshot.glossary);
        replace(timelineRecordedAt, snapshot.timelineRecordedAt);
        timelineSequence.set(snapshot.timelineSequence);
    }

    private static <T> Map<String, T> copy(ConcurrentHashMap<String, T> source) {
        return new LinkedHashMap<>(source);
    }

    private static <T> void replace(ConcurrentHashMap<String, T> target, Map<String, T> values) {
        target.clear();
        target.putAll(values);
    }

    public record Snapshot(
            Map<String, RuntimeCharacter> characters,
            Map<String, RuntimeTerritory> territories,
            Map<String, RuntimePlace> places,
            Map<String, RuntimeStory> stories,
            Map<String, RuntimeRelationship> relationships,
            Map<String, RuntimeOrganization> organizations,
            Map<String, RuntimeResource> resources,
            Map<String, RuntimeObject> objects,
            Map<String, RuntimeTimelineEntry> timeline,
            Map<String, RuntimePromptProfile> promptProfiles,
            Map<String, RuntimeCanon> canon,
            Map<String, RuntimeWorldRules> worldRules,
            Map<String, RuntimeLaw> laws,
            Map<String, RuntimeCustom> customs,
            Map<String, RuntimeGlossaryEntry> glossary,
            Map<String, Instant> timelineRecordedAt,
            long timelineSequence) {}
}
