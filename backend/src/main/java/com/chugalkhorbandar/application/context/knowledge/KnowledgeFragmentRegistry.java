package com.chugalkhorbandar.application.context.knowledge;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class KnowledgeFragmentRegistry {

    private final Map<String, KnowledgeFragment> byId = new LinkedHashMap<>();
    private final Map<KnowledgeFragmentType, List<KnowledgeFragment>> byType = new LinkedHashMap<>();
    private final Map<String, List<KnowledgeFragment>> byTag = new LinkedHashMap<>();
    private final Map<String, List<KnowledgeFragment>> bySource = new LinkedHashMap<>();
    private final Map<String, List<KnowledgeFragment>> byEntity = new LinkedHashMap<>();

    public void register(KnowledgeFragment fragment) {
        byId.put(fragment.fragmentId(), fragment);
        byType.computeIfAbsent(fragment.fragmentType(), ignored -> new ArrayList<>()).add(fragment);
        fragment.tags().forEach(tag -> byTag.computeIfAbsent(tag, ignored -> new ArrayList<>()).add(fragment));
        bySource.computeIfAbsent(fragment.sourceDocument(), ignored -> new ArrayList<>()).add(fragment);
        byEntity.computeIfAbsent(entityKey(fragment.sourceDocument()), ignored -> new ArrayList<>())
                .add(fragment);
    }

    public void registerAll(List<KnowledgeFragment> fragments) {
        fragments.forEach(this::register);
    }

    public void clear() {
        byId.clear();
        byType.clear();
        byTag.clear();
        bySource.clear();
        byEntity.clear();
    }

    public Optional<KnowledgeFragment> findById(String fragmentId) {
        return Optional.ofNullable(byId.get(fragmentId));
    }

    public List<KnowledgeFragment> findByType(KnowledgeFragmentType type) {
        return List.copyOf(byType.getOrDefault(type, List.of()));
    }

    public List<KnowledgeFragment> findByTag(String tag) {
        return List.copyOf(byTag.getOrDefault(tag, List.of()));
    }

    public List<KnowledgeFragment> findBySource(String sourceDocument) {
        return List.copyOf(bySource.getOrDefault(sourceDocument, List.of()));
    }

    public List<KnowledgeFragment> findByEntity(String entityId) {
        return List.copyOf(byEntity.getOrDefault(entityId, List.of()));
    }

    public List<KnowledgeFragment> allFragments() {
        return byId.values().stream()
                .sorted(Comparator.comparingInt(fragment -> KnowledgeFragmentPriorities.priority(fragment.fragmentType())))
                .toList();
    }

    private static String entityKey(String sourceDocument) {
        int separator = sourceDocument.indexOf(':');
        return separator < 0 ? sourceDocument : sourceDocument.substring(0, separator);
    }
}
