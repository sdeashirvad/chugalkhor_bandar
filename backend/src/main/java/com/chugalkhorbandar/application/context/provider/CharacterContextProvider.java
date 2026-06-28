package com.chugalkhorbandar.application.context.provider;



import com.chugalkhorbandar.application.context.ContextPlannerRequest;

import com.chugalkhorbandar.application.context.ContextReference;

import com.chugalkhorbandar.application.context.ContextSection;

import com.chugalkhorbandar.application.context.ContextSectionPriorities;

import com.chugalkhorbandar.application.context.ContextSectionType;

import com.chugalkhorbandar.application.context.resolver.ResolvedContextSection;

import com.chugalkhorbandar.application.query.EntityReferenceResolver;

import com.chugalkhorbandar.application.query.TextSectionSupport;

import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;

import com.chugalkhorbandar.domain.world.runtime.RuntimePlace;

import java.util.ArrayList;

import java.util.List;

import java.util.Optional;

import java.util.Set;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;



@Component

public class CharacterContextProvider implements ContextProvider {



    private final WorldRepositoryProvider repositories;

    private final EntityReferenceResolver referenceResolver;



    public CharacterContextProvider(WorldRepositoryProvider repositories, EntityReferenceResolver referenceResolver) {

        this.repositories = repositories;

        this.referenceResolver = referenceResolver;

    }



    @Override

    public String providerName() {

        return "characters";

    }



    @Override

    public Set<ContextSectionType> supportedTypes() {

        return Set.of(ContextSectionType.CURRENT_CHARACTER, ContextSectionType.CURRENT_LOCATION);

    }



    @Override

    public List<ContextSection> plan(ContextPlannerRequest request, Set<ContextSectionType> selectedTypes) {

        List<ContextSection> sections = new ArrayList<>();

        String characterId = request.currentCharacter().id();

        if (selectedTypes.contains(ContextSectionType.CURRENT_CHARACTER)) {

            sections.add(section(

                    ContextSectionType.CURRENT_CHARACTER,

                    providerName(),

                    new ContextReference(providerName(), "character", characterId, "profile", priority(ContextSectionType.CURRENT_CHARACTER))));

        }

        if (selectedTypes.contains(ContextSectionType.CURRENT_LOCATION)) {

            String placeId = repositories.characters().findById(characterId)

                    .flatMap(character -> Optional.ofNullable(character.currentPlaceId()))

                    .orElse("none");

            sections.add(section(

                    ContextSectionType.CURRENT_LOCATION,

                    "places",

                    new ContextReference("places", "place", placeId, "details", priority(ContextSectionType.CURRENT_LOCATION))));

        }

        return sections;

    }



    @Override

    public boolean supports(ContextReference reference) {

        return Set.of("characters", "places").contains(reference.provider());

    }



    @Override

    public ResolvedContextSection resolve(ContextSection section, ContextPlannerRequest request) {

        if (section.type() == ContextSectionType.CURRENT_CHARACTER) {

            return repositories.characters().findById(section.reference().entityId())

                    .map(character -> {

                        var publicSections = TextSectionSupport.publicSections(character.sections());

                        String content = character.title()

                                + "\n"

                                + publicSections.entrySet().stream()

                                        .map(entry -> entry.getKey() + ": " + entry.getValue().trim())

                                        .collect(Collectors.joining("\n"));

                        return ResolvedContextSection.from(section, content);

                    })

                    .orElseGet(() -> ResolvedContextSection.from(section, "[missing: " + section.reference().entityId() + "]"));

        }

        if ("none".equals(section.reference().entityId())) {

            return ResolvedContextSection.from(section, "Current location is unknown.");

        }

        return repositories.places().findById(section.reference().entityId())

                .map(place -> ResolvedContextSection.from(section, formatPlaceContent(place)))

                .orElseGet(() -> ResolvedContextSection.from(section, "[missing: " + section.reference().entityId() + "]"));

    }



    private String formatPlaceContent(RuntimePlace place) {

        StringBuilder content = new StringBuilder(place.title());



        appendSectionLine(content, "Type", place.sections().get("type"));



        String locatedIn = place.sections().get("locatedIn");

        if (locatedIn != null && !locatedIn.isBlank()) {

            content.append("\nLocated in: ").append(locatedIn.trim());

            resolvePlaceByName(locatedIn).ifPresent(parent -> appendParentPlaceContext(content, parent));

        }



        referenceResolver.resolveTerritoryForPlace(place)

                .ifPresent(territory -> content.append("\nKingdom/Territory: ").append(territory.name()));



        appendSectionLine(content, "Description", place.sections().get("description"));



        return content.toString().trim();

    }



    private void appendParentPlaceContext(StringBuilder content, RuntimePlace parent) {

        if (parent.sections().get("description") != null && !parent.sections().get("description").isBlank()) {

            content.append("\n").append(parent.title()).append(": ").append(parent.sections().get("description").trim());

        }

    }



    private Optional<RuntimePlace> resolvePlaceByName(String name) {

        return repositories.places().findAll().stream()

                .filter(place -> namesMatch(place.title(), name) || titleContains(place.title(), name))

                .findFirst();

    }



    private static void appendSectionLine(StringBuilder content, String label, String value) {

        if (value != null && !value.isBlank()) {

            content.append("\n").append(label).append(": ").append(value.trim());

        }

    }



    private static boolean namesMatch(String left, String right) {

        if (left == null || right == null) {

            return false;

        }

        return left.trim().equalsIgnoreCase(right.trim());

    }



    private static boolean titleContains(String title, String name) {

        if (title == null || name == null) {

            return false;

        }

        return title.toLowerCase().contains(name.trim().toLowerCase());

    }



    private static int priority(ContextSectionType type) {

        return ContextSectionPriorities.priority(type);

    }

}

