package com.chugalkhorbandar.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ChugalkhorProperties.class, LlmProperties.class, GroqProperties.class, PromptProfileProperties.class, com.chugalkhorbandar.application.memory.working.WorkingMemoryProperties.class, com.chugalkhorbandar.application.conversation.director.ConversationDirectorProperties.class, com.chugalkhorbandar.application.notification.NotificationProperties.class, com.chugalkhorbandar.application.artifacts.ConversationArtifactProperties.class, com.chugalkhorbandar.application.cognition.CognitiveAnalysisProperties.class, com.chugalkhorbandar.application.memory.inbox.MemoryInboxProperties.class, com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationProperties.class, com.chugalkhorbandar.application.email.ReportEmailProperties.class, com.chugalkhorbandar.application.reporting.ReportingProperties.class, com.chugalkhorbandar.application.reporting.ReportingBrandingProperties.class, com.chugalkhorbandar.application.reporting.ReportingAttachmentProperties.class, com.chugalkhorbandar.application.chronicle.ChronicleWriterProperties.class})
public class ApplicationConfiguration {
}
