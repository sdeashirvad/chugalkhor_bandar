package com.chugalkhorbandar.application.reporting;

import com.chugalkhorbandar.application.email.ReportEmailProperties;
import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationReport;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ReportingEngine {

    private final ReportingProperties properties;
    private final ReportEmailProperties emailProperties;
    private final ReportTemplateLoader templateLoader;
    private final ReportingTemplateRenderer templateRenderer;
    private final ReportContextBuilder contextBuilder;

    public ReportingEngine(
            ReportingProperties properties,
            ReportEmailProperties emailProperties,
            ReportTemplateLoader templateLoader,
            ReportingTemplateRenderer templateRenderer,
            ReportContextBuilder contextBuilder) {
        this.properties = properties;
        this.emailProperties = emailProperties;
        this.templateLoader = templateLoader;
        this.templateRenderer = templateRenderer;
        this.contextBuilder = contextBuilder;
    }

    public ReportArchive buildArchive(MemoryConsolidationReport report) {
        List<String> attachmentNames = buildAttachmentNames();
        ReportTemplateContext context = contextBuilder.build(report, attachmentNames);
        String html = templateRenderer.render(templateLoader.loadTemplate("email/daily-report.html"), context);
        String txt = templateRenderer.render(templateLoader.loadTemplate("email/daily-report.txt"), context);
        String markdown = templateRenderer.render(templateLoader.loadTemplate("email/daily-report.md"), context);
        return new ReportArchive(
                report.runId(),
                html,
                txt,
                report.jsonReport(),
                markdown,
                Instant.now());
    }

    public ReportingEmailPayload buildEmailPayload(MemoryConsolidationReport report, ReportArchive archive) {
        List<ReportingAttachment> attachments = buildAttachments(archive);
        ReportTemplateContext context = contextBuilder.build(
                report, attachments.stream().map(ReportingAttachment::filename).toList());
        String subject = SubjectTemplateRenderer.render(
                emailProperties.getSubjectTemplate() != null && !emailProperties.getSubjectTemplate().isBlank()
                        ? emailProperties.getSubjectTemplate()
                        : properties.getSubjectTemplate(),
                context);
        return new ReportingEmailPayload(
                FriendlySenderParser.parse(emailProperties.getFrom()),
                RecipientParser.parse(emailProperties.getTo()),
                subject,
                archive.htmlContent(),
                attachments);
    }

    private List<String> buildAttachmentNames() {
        ReportingAttachmentProperties attachmentProperties = properties.getAttachments();
        List<String> names = new ArrayList<>();
        if (attachmentProperties.isTxt()) {
            names.add("jungle-daily-report.txt");
        }
        if (attachmentProperties.isJson()) {
            names.add("jungle-daily-report.json");
        }
        if (attachmentProperties.isMd()) {
            names.add("jungle-daily-report.md");
        }
        if (attachmentProperties.isHtml()) {
            names.add("jungle-daily-report.html");
        }
        return List.copyOf(names);
    }

    private List<ReportingAttachment> buildAttachments(ReportArchive archive) {
        ReportingAttachmentProperties attachmentProperties = properties.getAttachments();
        List<ReportingAttachment> attachments = new ArrayList<>();
        if (attachmentProperties.isTxt()) {
            attachments.add(new ReportingAttachment("jungle-daily-report.txt", "text/plain", archive.txtContent()));
        }
        if (attachmentProperties.isJson()) {
            attachments.add(new ReportingAttachment("jungle-daily-report.json", "application/json", archive.jsonContent()));
        }
        if (attachmentProperties.isMd()) {
            attachments.add(new ReportingAttachment("jungle-daily-report.md", "text/markdown", archive.markdownContent()));
        }
        if (attachmentProperties.isHtml()) {
            attachments.add(new ReportingAttachment("jungle-daily-report.html", "text/html", archive.htmlContent()));
        }
        return List.copyOf(attachments);
    }
}
