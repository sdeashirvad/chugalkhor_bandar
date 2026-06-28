package com.chugalkhorbandar.application.reporting;

public record ReportTemplateContext(
        String date,
        String greeting,
        String reflection,
        String reflectionSection,
        String reflectionSectionHtml,
        String conversations,
        String artifacts,
        String inboxItems,
        String promoted,
        String discarded,
        String candidates,
        String pending,
        String pendingPromises,
        String unreadNotifications,
        String closing,
        String attachmentsList,
        String attachmentsListHtml,
        String statCards) {}
