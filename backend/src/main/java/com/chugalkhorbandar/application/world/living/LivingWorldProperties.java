package com.chugalkhorbandar.application.world.living;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "chugalkhor.living-world")
public class LivingWorldProperties {

    private boolean enabled = true;
    private boolean hourlyEnabled = true;
    private boolean dailyEnabled = true;
    private boolean festivalEnabled = true;
    private boolean birthdayEnabled = true;
    private boolean promiseEngineEnabled = true;
    private boolean characterInitiativeEnabled = true;
    private boolean gossipEnabled = true;
    private boolean manualTickEnabled = true;
    private String schedule = "0 0 * * * *";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isHourlyEnabled() {
        return hourlyEnabled;
    }

    public void setHourlyEnabled(boolean hourlyEnabled) {
        this.hourlyEnabled = hourlyEnabled;
    }

    public boolean isDailyEnabled() {
        return dailyEnabled;
    }

    public void setDailyEnabled(boolean dailyEnabled) {
        this.dailyEnabled = dailyEnabled;
    }

    public boolean isFestivalEnabled() {
        return festivalEnabled;
    }

    public void setFestivalEnabled(boolean festivalEnabled) {
        this.festivalEnabled = festivalEnabled;
    }

    public boolean isBirthdayEnabled() {
        return birthdayEnabled;
    }

    public void setBirthdayEnabled(boolean birthdayEnabled) {
        this.birthdayEnabled = birthdayEnabled;
    }

    public boolean isPromiseEngineEnabled() {
        return promiseEngineEnabled;
    }

    public void setPromiseEngineEnabled(boolean promiseEngineEnabled) {
        this.promiseEngineEnabled = promiseEngineEnabled;
    }

    public boolean isCharacterInitiativeEnabled() {
        return characterInitiativeEnabled;
    }

    public void setCharacterInitiativeEnabled(boolean characterInitiativeEnabled) {
        this.characterInitiativeEnabled = characterInitiativeEnabled;
    }

    public boolean isGossipEnabled() {
        return gossipEnabled;
    }

    public void setGossipEnabled(boolean gossipEnabled) {
        this.gossipEnabled = gossipEnabled;
    }

    public boolean isManualTickEnabled() {
        return manualTickEnabled;
    }

    public void setManualTickEnabled(boolean manualTickEnabled) {
        this.manualTickEnabled = manualTickEnabled;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }
}
