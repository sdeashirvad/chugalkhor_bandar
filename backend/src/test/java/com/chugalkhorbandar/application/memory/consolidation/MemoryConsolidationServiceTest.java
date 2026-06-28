package com.chugalkhorbandar.application.memory.consolidation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.chugalkhorbandar.adapters.persistence.memory.InMemoryLongTermMemoryCandidateRepository;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryMemoryConsolidationReportRepository;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryMemoryConsolidationStore;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryMemoryInboxRepository;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryMemoryInboxStore;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryDeliveryHistoryRepository;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryReportArchiveRepository;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryReportingStore;
import com.chugalkhorbandar.application.email.ReportEmailProperties;
import com.chugalkhorbandar.application.reporting.ReportingEngine;
import com.chugalkhorbandar.application.reporting.ReportingProperties;
import com.chugalkhorbandar.application.reporting.ReportingService;
import com.chugalkhorbandar.application.reporting.ReportingTemplateRenderer;
import com.chugalkhorbandar.application.reporting.ReportContextBuilder;
import com.chugalkhorbandar.application.reporting.ReportTemplateLoader;
import com.chugalkhorbandar.application.reporting.ResendReportDeliveryProvider;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxImportance;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxItem;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxSource;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxStatus;
import com.chugalkhorbandar.application.query.WorldStatus;
import com.chugalkhorbandar.application.query.WorldStatusQueryService;
import com.chugalkhorbandar.config.LlmProperties;
import com.chugalkhorbandar.domain.artifacts.ports.ConversationArtifactRepository;
import com.chugalkhorbandar.domain.notification.ports.NotificationRepository;
import com.chugalkhorbandar.domain.world.ports.CharacterRepository;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import com.chugalkhorbandar.domain.world.ports.query.CharacterQuery;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCharacter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemoryConsolidationServiceTest {

    @Mock
    private ConversationArtifactRepository artifactRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private WorldRepositoryProvider worldRepositoryProvider;

    @Mock
    private CharacterRepository characterRepository;

    @Mock
    private WorldStatusQueryService worldStatusQueryService;

    private MemoryConsolidationService service;
    private InMemoryMemoryInboxRepository inboxRepository;

    @BeforeEach
    void setUp() {
        MemoryConsolidationProperties properties = new MemoryConsolidationProperties();
        properties.setReflectionEnabled(true);
        InMemoryMemoryConsolidationStore store = new InMemoryMemoryConsolidationStore();
        inboxRepository = new InMemoryMemoryInboxRepository(new InMemoryMemoryInboxStore());
        MemoryConsolidationReflectionService reflectionService = new MemoryConsolidationReflectionService(
                properties,
                new LlmProperties(),
                new com.chugalkhorbandar.application.llm.groq.GroqKeyPool(List.of()),
                new com.chugalkhorbandar.application.llm.groq.GroqHttpClient(
                        new ObjectMapper(), "https://api.groq.com/openai/v1", 30, null));
        InMemoryReportingStore reportingStore = new InMemoryReportingStore();
        ReportingProperties reportingProperties = new ReportingProperties();
        ReportEmailProperties emailProperties = new ReportEmailProperties();
        ObjectMapper objectMapper = new ObjectMapper();
        ReportingService reportingService = new ReportingService(
                reportingProperties,
                emailProperties,
                new ReportingEngine(
                        reportingProperties,
                        emailProperties,
                        new ReportTemplateLoader(),
                        new ReportingTemplateRenderer(),
                        new ReportContextBuilder(reportingProperties, objectMapper)),
                new ResendReportDeliveryProvider(emailProperties, objectMapper),
                new InMemoryReportArchiveRepository(reportingStore),
                new InMemoryDeliveryHistoryRepository(reportingStore));
        service = new MemoryConsolidationService(
                properties,
                new MemoryConsolidationEngine(properties),
                new MemoryConsolidationReportGenerator(),
                reflectionService,
                reportingService,
                inboxRepository,
                new InMemoryLongTermMemoryCandidateRepository(store),
                new InMemoryMemoryConsolidationReportRepository(store),
                new MemoryConsolidationGenerationStore(),
                artifactRepository,
                notificationRepository,
                worldRepositoryProvider,
                worldStatusQueryService);
    }

    @Test
    void runsConsolidationAndPersistsReport() {
        stubWorld();
        inboxRepository.save(inboxItem("i-1", "PROMISE", "Remember", 0.9));

        MemoryConsolidationReport report = service.runConsolidation();

        assertThat(report.promoted()).isEqualTo(1);
        assertThat(report.candidateCount()).isEqualTo(1);
        assertThat(service.getLatestReport()).isPresent();
        assertThat(service.getAllCandidates()).hasSize(1);
        assertThat(inboxRepository.findById("i-1").orElseThrow().status()).isEqualTo(MemoryInboxStatus.ARCHIVED);
        assertThat(report.reflection()).isNotBlank();
    }

    @Test
    void skipsEmailWhenDisabled() {
        stubWorld();
        inboxRepository.save(inboxItem("i-1", "UNKNOWN", "Skip me", 0.9));

        MemoryConsolidationReport report = service.runConsolidation();

        assertThat(report.emailStatus()).isEqualTo("SKIPPED");
    }

    private void stubWorld() {
        when(worldRepositoryProvider.characters()).thenReturn(characterRepository);
        when(characterRepository.findAll(CharacterQuery.all()))
                .thenReturn(List.of(new RuntimeCharacter("character_alpha", "Alpha", Map.of(), null, Map.of())));
        when(artifactRepository.findAllForCharacter("character_alpha")).thenReturn(List.of());
        when(notificationRepository.countUnreadByRecipientCharacterId("character_alpha")).thenReturn(0L);
        when(worldStatusQueryService.getStatus())
                .thenReturn(new WorldStatus(
                        "READY",
                        "1.0",
                        Instant.parse("2026-06-01T00:00:00Z"),
                        Instant.parse("2026-06-01T12:00:00Z"),
                        "IN_MEMORY",
                        1,
                        1,
                        0,
                        0,
                        0,
                        0,
                        0,
                        Map.of(),
                        Map.of()));
    }

    private static MemoryInboxItem inboxItem(String id, String type, String summary, double confidence) {
        Instant now = Instant.parse("2026-06-01T12:00:00Z");
        return new MemoryInboxItem(
                id,
                type,
                MemoryInboxSource.CONVERSATION_ARTIFACT,
                "source-" + id,
                "character_alpha",
                summary,
                MemoryInboxImportance.HIGH,
                confidence,
                MemoryInboxStatus.NEW,
                now,
                now.plus(30, ChronoUnit.DAYS),
                Map.of("conversationId", "conv-1"),
                List.of("created"),
                "",
                List.of("art-1"));
    }
}
