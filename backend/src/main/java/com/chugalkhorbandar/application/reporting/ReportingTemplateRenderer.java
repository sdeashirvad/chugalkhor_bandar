package com.chugalkhorbandar.application.reporting;

import org.springframework.stereotype.Component;

@Component
public class ReportingTemplateRenderer {

    public String render(String template, ReportTemplateContext context) {
        return template
                .replace("{{greeting}}", context.greeting())
                .replace("{{reflection}}", context.reflection())
                .replace("{{reflectionSection}}", context.reflectionSection())
                .replace("{{reflectionSectionHtml}}", context.reflectionSectionHtml())
                .replace("{{date}}", context.date())
                .replace("{{conversations}}", context.conversations())
                .replace("{{artifacts}}", context.artifacts())
                .replace("{{inboxItems}}", context.inboxItems())
                .replace("{{promoted}}", context.promoted())
                .replace("{{discarded}}", context.discarded())
                .replace("{{candidates}}", context.candidates())
                .replace("{{pending}}", context.pending())
                .replace("{{pendingPromises}}", context.pendingPromises())
                .replace("{{unreadNotifications}}", context.unreadNotifications())
                .replace("{{closing}}", context.closing())
                .replace("{{attachmentsList}}", context.attachmentsList())
                .replace("{{attachmentsListHtml}}", context.attachmentsListHtml())
                .replace("{{statCards}}", context.statCards())
                .replace("{{subject}}", "Jungle Daily Report");
    }
}
