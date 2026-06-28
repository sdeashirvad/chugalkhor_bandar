package com.chugalkhorbandar.adapters.persistence.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "report_archives")
public class ReportArchiveEntity {

    @Id
    @Column(name = "report_id", nullable = false)
    private String reportId;

    @Column(name = "html_content", nullable = false, columnDefinition = "TEXT")
    private String htmlContent;

    @Column(name = "txt_content", nullable = false, columnDefinition = "TEXT")
    private String txtContent;

    @Column(name = "json_content", nullable = false, columnDefinition = "TEXT")
    private String jsonContent;

    @Column(name = "markdown_content", nullable = false, columnDefinition = "TEXT")
    private String markdownContent;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ReportArchiveEntity() {}

    public ReportArchiveEntity(
            String reportId,
            String htmlContent,
            String txtContent,
            String jsonContent,
            String markdownContent,
            Instant createdAt) {
        this.reportId = reportId;
        this.htmlContent = htmlContent;
        this.txtContent = txtContent;
        this.jsonContent = jsonContent;
        this.markdownContent = markdownContent;
        this.createdAt = createdAt;
    }

    public String getReportId() {
        return reportId;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public String getTxtContent() {
        return txtContent;
    }

    public String getJsonContent() {
        return jsonContent;
    }

    public String getMarkdownContent() {
        return markdownContent;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
