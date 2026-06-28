package com.chugalkhorbandar.bootstrap.compiler;

import com.chugalkhorbandar.bootstrap.compiler.command.BootstrapCommand;
import com.chugalkhorbandar.bootstrap.typed.BootstrapTypedWorld;
import com.chugalkhorbandar.bootstrap.typed.spec.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class BootstrapCompiler {

    public BootstrapCompilation compile(BootstrapTypedWorld world) {
        if (world == null) {
            throw new BootstrapCompilationException("Cannot compile null BootstrapTypedWorld");
        }

        long startNanos = System.nanoTime();
        List<String> warnings = new ArrayList<>();
        List<BootstrapCommand> commands = new ArrayList<>();
        Map<String, Integer> counts = new LinkedHashMap<>();
        int executionOrder = 0;

        executionOrder = appendCanon(world, commands, counts, executionOrder);
        executionOrder = appendWorldRules(world, commands, counts, executionOrder);
        executionOrder = appendPromptProfiles(world, commands, counts, executionOrder);
        executionOrder = appendTerritories(world, commands, counts, executionOrder);
        executionOrder = appendPlaces(world, commands, counts, executionOrder);
        executionOrder = appendOrganizations(world, commands, counts, executionOrder);
        executionOrder = appendResources(world, commands, counts, executionOrder);
        executionOrder = appendObjects(world, commands, counts, executionOrder);
        executionOrder = appendCharacters(world, commands, counts, executionOrder);
        executionOrder = appendRelationships(world, commands, counts, executionOrder);
        executionOrder = appendStories(world, commands, counts, executionOrder);
        executionOrder = appendChronology(world, commands, counts, executionOrder);
        executionOrder = appendLaws(world, commands, counts, executionOrder);
        executionOrder = appendCustoms(world, commands, counts, executionOrder);
        appendGlossary(world, commands, counts, executionOrder);

        validateDuplicateCommandIds(commands);

        long durationMillis = (System.nanoTime() - startNanos) / 1_000_000;
        BootstrapCompilationReport report =
                new BootstrapCompilationReport(counts, durationMillis, warnings, true);
        return new BootstrapCompilation(List.copyOf(commands), warnings, report);
    }

    private static int appendCanon(
            BootstrapTypedWorld world, List<BootstrapCommand> commands, Map<String, Integer> counts, int order) {
        List<CanonBootstrapSpec> specs = sorted(world.canon(), Comparator.comparing(CanonBootstrapSpec::id));
        for (CanonBootstrapSpec spec : specs) {
            commands.add(BootstrapCommandMapper.toCanonCommand(spec, order++));
        }
        counts.put("Canon", specs.size());
        return order;
    }

    private static int appendWorldRules(
            BootstrapTypedWorld world, List<BootstrapCommand> commands, Map<String, Integer> counts, int order) {
        List<WorldRulesBootstrapSpec> specs =
                sorted(world.worldRules(), Comparator.comparing(WorldRulesBootstrapSpec::id));
        for (WorldRulesBootstrapSpec spec : specs) {
            commands.add(BootstrapCommandMapper.toWorldRulesCommand(spec, order++));
        }
        counts.put("World Rules", specs.size());
        return order;
    }

    private static int appendPromptProfiles(
            BootstrapTypedWorld world, List<BootstrapCommand> commands, Map<String, Integer> counts, int order) {
        List<PromptProfileBootstrapSpec> specs =
                sorted(world.promptProfiles(), Comparator.comparing(PromptProfileBootstrapSpec::id));
        for (PromptProfileBootstrapSpec spec : specs) {
            commands.add(BootstrapCommandMapper.toPromptProfileCommand(spec, order++));
        }
        counts.put("Prompt Profiles", specs.size());
        return order;
    }

    private static int appendTerritories(
            BootstrapTypedWorld world, List<BootstrapCommand> commands, Map<String, Integer> counts, int order) {
        List<TerritoryBootstrapSpec> specs =
                sorted(world.territories(), Comparator.comparing(TerritoryBootstrapSpec::id));
        for (TerritoryBootstrapSpec spec : specs) {
            commands.add(BootstrapCommandMapper.toTerritoryCommand(spec, order++));
        }
        counts.put("Territories", specs.size());
        return order;
    }

    private static int appendPlaces(
            BootstrapTypedWorld world, List<BootstrapCommand> commands, Map<String, Integer> counts, int order) {
        List<PlaceBootstrapSpec> specs = sorted(world.places(), Comparator.comparing(PlaceBootstrapSpec::id));
        for (PlaceBootstrapSpec spec : specs) {
            commands.add(BootstrapCommandMapper.toPlaceCommand(spec, order++));
        }
        counts.put("Places", specs.size());
        return order;
    }

    private static int appendOrganizations(
            BootstrapTypedWorld world, List<BootstrapCommand> commands, Map<String, Integer> counts, int order) {
        List<OrganizationBootstrapSpec> specs =
                sorted(world.organizations(), Comparator.comparing(OrganizationBootstrapSpec::id));
        for (OrganizationBootstrapSpec spec : specs) {
            commands.add(BootstrapCommandMapper.toOrganizationCommand(spec, order++));
        }
        counts.put("Organizations", specs.size());
        return order;
    }

    private static int appendResources(
            BootstrapTypedWorld world, List<BootstrapCommand> commands, Map<String, Integer> counts, int order) {
        List<ResourceBootstrapSpec> specs = sorted(world.resources(), Comparator.comparing(ResourceBootstrapSpec::id));
        for (ResourceBootstrapSpec spec : specs) {
            commands.add(BootstrapCommandMapper.toResourceCommand(spec, order++));
        }
        counts.put("Resources", specs.size());
        return order;
    }

    private static int appendObjects(
            BootstrapTypedWorld world, List<BootstrapCommand> commands, Map<String, Integer> counts, int order) {
        List<ObjectBootstrapSpec> specs = sorted(world.objects(), Comparator.comparing(ObjectBootstrapSpec::id));
        for (ObjectBootstrapSpec spec : specs) {
            commands.add(BootstrapCommandMapper.toObjectCommand(spec, order++));
        }
        counts.put("Objects", specs.size());
        return order;
    }

    private static int appendCharacters(
            BootstrapTypedWorld world, List<BootstrapCommand> commands, Map<String, Integer> counts, int order) {
        List<CharacterBootstrapSpec> specs =
                sorted(world.characters(), Comparator.comparing(CharacterBootstrapSpec::id));
        for (CharacterBootstrapSpec spec : specs) {
            commands.add(BootstrapCommandMapper.toCharacterCommand(spec, order++));
        }
        counts.put("Characters", specs.size());
        return order;
    }

    private static int appendRelationships(
            BootstrapTypedWorld world, List<BootstrapCommand> commands, Map<String, Integer> counts, int order) {
        List<RelationshipBootstrapSpec> specs =
                sorted(world.relationships(), Comparator.comparing(RelationshipBootstrapSpec::id));
        for (RelationshipBootstrapSpec spec : specs) {
            commands.add(BootstrapCommandMapper.toRelationshipCommand(spec, order++));
        }
        counts.put("Relationships", specs.size());
        return order;
    }

    private static int appendStories(
            BootstrapTypedWorld world, List<BootstrapCommand> commands, Map<String, Integer> counts, int order) {
        List<StoryBootstrapSpec> specs = sorted(world.stories(), Comparator.comparing(StoryBootstrapSpec::id));
        for (StoryBootstrapSpec spec : specs) {
            commands.add(BootstrapCommandMapper.toStoryCommand(spec, order++));
        }
        counts.put("Stories", specs.size());
        return order;
    }

    private static int appendChronology(
            BootstrapTypedWorld world, List<BootstrapCommand> commands, Map<String, Integer> counts, int order) {
        List<ChronologyBootstrapSpec> specs =
                sorted(world.chronologyEntries(), Comparator.comparing(ChronologyBootstrapSpec::id));
        for (ChronologyBootstrapSpec spec : specs) {
            commands.add(BootstrapCommandMapper.toChronologyCommand(spec, order++));
        }
        counts.put("Chronology", specs.size());
        return order;
    }

    private static int appendLaws(
            BootstrapTypedWorld world, List<BootstrapCommand> commands, Map<String, Integer> counts, int order) {
        List<LawBootstrapSpec> specs = sorted(world.laws(), Comparator.comparing(LawBootstrapSpec::id));
        for (LawBootstrapSpec spec : specs) {
            commands.add(BootstrapCommandMapper.toLawCommand(spec, order++));
        }
        counts.put("Laws", specs.size());
        return order;
    }

    private static int appendCustoms(
            BootstrapTypedWorld world, List<BootstrapCommand> commands, Map<String, Integer> counts, int order) {
        List<CustomBootstrapSpec> specs = sorted(world.customs(), Comparator.comparing(CustomBootstrapSpec::id));
        for (CustomBootstrapSpec spec : specs) {
            commands.add(BootstrapCommandMapper.toCustomCommand(spec, order++));
        }
        counts.put("Customs", specs.size());
        return order;
    }

    private static void appendGlossary(
            BootstrapTypedWorld world, List<BootstrapCommand> commands, Map<String, Integer> counts, int order) {
        List<GlossaryEntryBootstrapSpec> specs =
                sorted(world.glossaryEntries(), Comparator.comparing(GlossaryEntryBootstrapSpec::id));
        for (GlossaryEntryBootstrapSpec spec : specs) {
            commands.add(BootstrapCommandMapper.toGlossaryEntryCommand(spec, order++));
        }
        counts.put("Glossary", specs.size());
    }

    private static <T> List<T> sorted(List<T> items, Comparator<T> comparator) {
        return items.stream().sorted(comparator).toList();
    }

    private static void validateDuplicateCommandIds(List<BootstrapCommand> commands) {
        Set<String> seen = new HashSet<>();
        for (BootstrapCommand command : commands) {
            if (!seen.add(command.commandId())) {
                throw new BootstrapCompilationException("Duplicate command id detected: " + command.commandId());
            }
        }
    }
}
