package com.chugalkhorbandar.application.context.knowledge.provider;

import com.chugalkhorbandar.application.context.ContextPlannerRequest;
import com.chugalkhorbandar.application.context.ContextReference;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragment;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentMappings;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentPriorities;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentRequest;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentType;
import com.chugalkhorbandar.application.query.EntityReferenceResolver;
import com.chugalkhorbandar.application.query.TextSectionSupport;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import com.chugalkhorbandar.domain.world.runtime.RuntimePlace;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class CharacterKnowledgeProvider implements KnowledgeProvider {

    private static final Set<KnowledgeFragmentType> SUPPORTED = Set.of(
            KnowledgeFragmentType.CHARACTER_PROFILE,
            KnowledgeFragmentType.CHARACTER_LOCATION,
            KnowledgeFragmentType.CHARACTER_TITLES,
            KnowledgeFragmentType.CHARACTER_RELATIONSHIPS,
            KnowledgeFragmentType.CHARACTER_PREFERENCES);

    private final WorldRepositoryProvider repositories;
    private final EntityReferenceResolver referenceResolver;

    public CharacterKnowledgeProvider(WorldRepositoryProvider repositories, EntityReferenceResolver referenceResolver) {
        this.repositories = repositories;
        this.referenceResolver = referenceResolver;
    }

    @Override
    public String providerName() {
        return "characterKnowledge";
    }

    @Override
    public Set<KnowledgeFragmentType> supportedFragmentTypes() {
        return SUPPORTED;
    }

    @Override
    public List<KnowledgeFragmentRequest> plan(ContextPlannerRequest request, Set<KnowledgeFragmentType> selectedTypes) {
        String characterId = request.currentCharacter().id();
        List<KnowledgeFragmentRequest> requests = new ArrayList<>();
        for (KnowledgeFragmentType type : selectedTypes) {
            if (!SUPPORTED.contains(type)) {
                continue;
            }
            if (type == KnowledgeFragmentType.CHARACTER_LOCATION) {
                String placeId = repositories.characters().findById(characterId)
                        .map(character -> character.currentPlaceId())
                        .orElse("none");
                requests.add(fragmentRequest(type, "place", placeId, characterId));
            } else {
                requests.add(fragmentRequest(type, "character", characterId, characterId));
            }
        }
        return requests;
    }

    @Override
    public Optional<KnowledgeFragment> resolve(KnowledgeFragmentRequest request, ContextPlannerRequest context) {
        return switch (request.fragmentType()) {
            case CHARACTER_LOCATION -> resolveLocation(request, context);
            case CHARACTER_PROFILE, CHARACTER_TITLES, CHARACTER_RELATIONSHIPS, CHARACTER_PREFERENCES ->
                    resolveCharacterSection(request, context.currentCharacter().id());
            default -> Optional.empty();
        };
    }

    private Optional<KnowledgeFragment> resolveCharacterSection(KnowledgeFragmentRequest request, String characterId) {
        return repositories.characters().findById(characterId).flatMap(character -> {
            Map<String, String> publicSections = TextSectionSupport.publicSections(character.sections());
            return publicSections.entrySet().stream()
                    .map(entry -> Map.entry(KnowledgeFragmentMappings.characterSectionType(entry.getKey()), entry))
                    .filter(entry -> entry.getKey() == request.fragmentType())
                    .findFirst()
                    .map(entry -> KnowledgeFragment.of(
                            request.fragmentType(),
                            titleFor(request.fragmentType(), character.title()),
                            perspectiveContent(
                                    request.fragmentType(), character.title(), entry.getValue().getValue()),
                            characterId,
                            entry.getValue().getKey(),
                            KnowledgeFragmentMappings.tagsFor(request.fragmentType(), characterId),
                            1.0));
        });
    }

    private Optional<KnowledgeFragment> resolveLocation(KnowledgeFragmentRequest request, ContextPlannerRequest context) {
        String placeId = request.reference().entityId();
        String characterName = context.currentCharacter().displayName();
        if ("none".equals(placeId)) {
            return Optional.of(KnowledgeFragment.of(
                    KnowledgeFragmentType.CHARACTER_LOCATION,
                    "Current Location",
                    perspectiveLocation(characterName, characterName + " is currently in an unknown location."),
                    context.currentCharacter().id(),
                    "currentPlace",
                    KnowledgeFragmentMappings.tagsFor(KnowledgeFragmentType.CHARACTER_LOCATION, context.currentCharacter().id()),
                    0.8));
        }
        return repositories.places().findById(placeId).map(place -> {
            String content = perspectiveLocation(
                    characterName, characterName + " is currently in " + place.title() + ".\n" + formatPlace(place));
            return KnowledgeFragment.of(
                    KnowledgeFragmentType.CHARACTER_LOCATION,
                    "Current Location",
                    content.trim(),
                    place.id(),
                    "details",
                    KnowledgeFragmentMappings.tagsFor(KnowledgeFragmentType.CHARACTER_LOCATION, context.currentCharacter().id()),
                    1.0);
        });
    }

    private String formatPlace(RuntimePlace place) {
        StringBuilder content = new StringBuilder();
        appendLine(content, "Type", place.sections().get("type"));
        appendResolvedReference(content, "Located in", place.sections().get("locatedIn"));
        appendLine(content, "Description", place.sections().get("description"));
        referenceResolver.resolveTerritoryForPlace(place)
                .ifPresent(territory -> content.append("Kingdom/Territory: ").append(territory.name()));
        return content.toString().trim();
    }

    private void appendResolvedReference(StringBuilder content, String label, String idOrName) {
        if (idOrName == null || idOrName.isBlank()) {
            return;
        }
        String resolved = referenceResolver.resolveTerritory(idOrName)
                .map(EntityReferenceResolver.ResolvedReference::name)
                .orElseGet(() -> referenceResolver.resolvePlace(idOrName)
                        .map(EntityReferenceResolver.ResolvedReference::name)
                        .orElse(idOrName.trim()));
        content.append(label).append(": ").append(resolved).append('\n');
    }

    private KnowledgeFragmentRequest fragmentRequest(
            KnowledgeFragmentType type, String entityType, String entityId, String characterId) {
        return new KnowledgeFragmentRequest(
                type,
                "Character knowledge fragment",
                KnowledgeFragmentPriorities.priority(type),
                new ContextReference(
                        providerName(),
                        entityType,
                        entityId,
                        type.name().toLowerCase(),
                        KnowledgeFragmentPriorities.priority(type)));
    }

    private static String titleFor(KnowledgeFragmentType type, String characterTitle) {
        return switch (type) {
            case CHARACTER_PROFILE -> characterTitle + " — What You Know";
            case CHARACTER_TITLES -> characterTitle + " — Titles You Recognize";
            case CHARACTER_RELATIONSHIPS -> characterTitle + " — Relationships You Know";
            case CHARACTER_PREFERENCES -> characterTitle + " — Preferences You Remember";
            default -> type.name();
        };
    }

    private static String perspectiveContent(KnowledgeFragmentType type, String characterName, String rawContent) {
        if (rawContent == null || rawContent.isBlank()) {
            return rawContent;
        }
        return switch (type) {
            case CHARACTER_PROFILE -> perspectiveProfile(characterName, rawContent);
            case CHARACTER_TITLES -> perspectiveTitles(characterName, rawContent);
            case CHARACTER_RELATIONSHIPS -> perspectiveRelationships(characterName, rawContent);
            default -> refocusOnSpeaker(characterName, rawContent);
        };
    }

    private static String perspectiveProfile(String characterName, String rawContent) {
        return """
                You know the current speaker well.
                They are %s.

                %s"""
                .formatted(characterName, refocusOnSpeaker(characterName, rawContent))
                .trim();
    }

    private static String perspectiveTitles(String characterName, String rawContent) {
        return """
                You recognize the titles held by the current speaker, %s:

                %s"""
                .formatted(characterName, rawContent.trim())
                .trim();
    }

    private static String perspectiveRelationships(String characterName, String rawContent) {
        return """
                From your own knowledge, these are relationships connected to %s:

                %s"""
                .formatted(characterName, rawContent.trim())
                .trim();
    }

    private static String perspectiveLocation(String characterName, String rawContent) {
        return """
                From what you know, the current speaker (%s) is located as follows:

                %s"""
                .formatted(characterName, refocusOnSpeaker(characterName, rawContent))
                .trim();
    }

    private static String refocusOnSpeaker(String characterName, String rawContent) {
        String refocused = rawContent.trim();
        refocused = Pattern.compile("^" + Pattern.quote(characterName) + "\\s+is\\b", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE)
                .matcher(refocused)
                .replaceAll("They are");
        refocused = Pattern.compile("^" + Pattern.quote(characterName) + "\\s+was\\b", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE)
                .matcher(refocused)
                .replaceAll("They were");
        refocused = Pattern.compile("^" + Pattern.quote(characterName) + "\\s+has\\b", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE)
                .matcher(refocused)
                .replaceAll("They have");
        return refocused;
    }

    private static void appendLine(StringBuilder content, String label, String value) {
        if (value != null && !value.isBlank()) {
            content.append(label).append(": ").append(value.trim()).append('\n');
        }
    }
}
