package com.chugalkhorbandar.application.reporting;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "chugalkhor.reporting.branding")
public class ReportingBrandingProperties {

    private String greetingName = "Ashirvad";

    public String getGreetingName() {
        return greetingName;
    }

    public void setGreetingName(String greetingName) {
        this.greetingName = greetingName;
    }
}
