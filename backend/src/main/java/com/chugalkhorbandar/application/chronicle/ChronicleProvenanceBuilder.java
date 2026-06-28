package com.chugalkhorbandar.application.chronicle;

import com.chugalkhorbandar.application.artifacts.ConversationArtifact;
import com.chugalkhorbandar.application.memory.consolidation.LongTermMemoryCandidate;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxItem;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxSource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ChronicleProvenanceBuilder {

    public ChronicleProvenance build(
            LongTermMemoryCandidate candidate,
            String chronicleId,
            List<MemoryInboxItem> inboxItems,
            List<ConversationArtifact> artifacts) {
        Set<String> artifactIds = new LinkedHashSet<>();
        Set<String> observationIds = new LinkedHashSet<>();
        String conversationId = "";
        List<ChronicleProvenanceLink> chain = new ArrayList<>();

        for (MemoryInboxItem inboxItem : inboxItems) {
            chain.add(new ChronicleProvenanceLink("INBOX_ITEM", inboxItem.id(), inboxItem.summary()));
            artifactIds.addAll(inboxItem.artifactIds());
            if (inboxItem.source() == MemoryInboxSource.COGNITIVE_OBSERVATION
                    || inboxItem.source() == MemoryInboxSource.COGNITIVE_RECOMMENDATION) {
                if (inboxItem.analysisId() != null && !inboxItem.analysisId().isBlank()) {
                    observationIds.add(inboxItem.analysisId());
                    chain.add(new ChronicleProvenanceLink(
                            "OBSERVATION", inboxItem.sourceId(), inboxItem.source().name()));
                }
            }
            String itemConversationId = inboxItem.metadata().getOrDefault("conversationId", "");
            if (!itemConversationId.isBlank()) {
                conversationId = itemConversationId;
            }
        }

        for (ConversationArtifact artifact : artifacts) {
            chain.add(new ChronicleProvenanceLink("ARTIFACT", artifact.id(), artifact.title()));
            if (conversationId.isBlank()) {
                conversationId = artifact.conversationId();
            }
        }

        if (!conversationId.isBlank()) {
            chain.add(0, new ChronicleProvenanceLink("CONVERSATION", conversationId, "Source conversation"));
        }
        if (!candidate.runId().isBlank()) {
            chain.add(new ChronicleProvenanceLink("CONSOLIDATION_RUN", candidate.runId(), "Consolidation run"));
        }
        chain.add(new ChronicleProvenanceLink("CANDIDATE", candidate.id(), candidate.summary()));
        chain.add(new ChronicleProvenanceLink("CHRONICLE", chronicleId, "Written chronicle"));

        Map<String, String> metadata = new LinkedHashMap<>(candidate.metadata());
        metadata.put("template", ChronicleBodyTemplateRenderer.templateName(
                ChronicleCategoryMapper.fromMetadata(candidate.metadata())));

        return new ChronicleProvenance(
                conversationId,
                List.copyOf(artifactIds),
                List.copyOf(observationIds),
                candidate.sourceInboxItems(),
                candidate.runId(),
                candidate.id(),
                chronicleId,
                chain,
                metadata);
    }
}
