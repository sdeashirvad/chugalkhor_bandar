package com.chugalkhorbandar.application.notification;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "chugalkhor.notifications")
public class NotificationProperties {

    private double dailyGreetingProbability = 0.8;
    private int notificationExpirationDays = 7;
    private int maximumActiveNotifications = 10;
    private boolean developerForceGeneration = false;
    private int recentVisitThresholdDays = 3;

    public double getDailyGreetingProbability() {
        return dailyGreetingProbability;
    }

    public void setDailyGreetingProbability(double dailyGreetingProbability) {
        this.dailyGreetingProbability = dailyGreetingProbability;
    }

    public int getNotificationExpirationDays() {
        return notificationExpirationDays;
    }

    public void setNotificationExpirationDays(int notificationExpirationDays) {
        this.notificationExpirationDays = notificationExpirationDays;
    }

    public int getMaximumActiveNotifications() {
        return maximumActiveNotifications;
    }

    public void setMaximumActiveNotifications(int maximumActiveNotifications) {
        this.maximumActiveNotifications = maximumActiveNotifications;
    }

    public boolean isDeveloperForceGeneration() {
        return developerForceGeneration;
    }

    public void setDeveloperForceGeneration(boolean developerForceGeneration) {
        this.developerForceGeneration = developerForceGeneration;
    }

    public int getRecentVisitThresholdDays() {
        return recentVisitThresholdDays;
    }

    public void setRecentVisitThresholdDays(int recentVisitThresholdDays) {
        this.recentVisitThresholdDays = recentVisitThresholdDays;
    }
}
