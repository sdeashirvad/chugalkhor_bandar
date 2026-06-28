package com.chugalkhorbandar.application.email;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class ReportEmailServiceTest {

    @Test
    void skipsWhenNotConfigured() {
        ReportEmailProperties properties = new ReportEmailProperties();
        properties.setEnabled(false);
        ReportEmailService service = new ReportEmailService(properties, new ObjectMapper());

        ReportEmailService.EmailDeliveryResult result =
                service.sendDailyReport("Subject", "<p>body</p>", "txt", "{}", "reflection");

        assertThat(result.status()).isEqualTo("SKIPPED");
    }
}
