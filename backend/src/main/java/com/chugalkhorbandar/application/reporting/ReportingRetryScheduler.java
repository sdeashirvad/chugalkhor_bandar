package com.chugalkhorbandar.application.reporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReportingRetryScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportingRetryScheduler.class);

    private final ReportingProperties properties;
    private final ReportingService reportingService;

    public ReportingRetryScheduler(ReportingProperties properties, ReportingService reportingService) {
        this.properties = properties;
        this.reportingService = reportingService;
    }

    @Scheduled(fixedDelayString = "${chugalkhor.reporting.retry-poll-ms:60000}")
    public void retryFailedDeliveries() {
        if (!properties.isEnabled() || !properties.isRetryEnabled()) {
            return;
        }
        ReportingDeliverySummary summary = reportingService.retryPendingDeliveries();
        if (summary.recipientsSent() > 0 || summary.recipientsFailed() > 0) {
            LOGGER.info(
                    "Reporting retry cycle complete: sent={}, failed={}",
                    summary.recipientsSent(),
                    summary.recipientsFailed());
        }
    }
}
