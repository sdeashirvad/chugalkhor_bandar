package com.chugalkhorbandar.config;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "prompt")
public class PromptProfileProperties {

    private BudgetProperties budget = new BudgetProperties();
    private Map<String, Map<String, Integer>> profiles = new LinkedHashMap<>();

    public BudgetProperties getBudget() {
        return budget;
    }

    public void setBudget(BudgetProperties budget) {
        this.budget = budget;
    }

    public Map<String, Map<String, Integer>> getProfiles() {
        return profiles;
    }

    public void setProfiles(Map<String, Map<String, Integer>> profiles) {
        this.profiles = profiles;
    }

    public static class BudgetProperties {
        private int minimumSectionTokens = 16;

        public int getMinimumSectionTokens() {
            return minimumSectionTokens;
        }

        public void setMinimumSectionTokens(int minimumSectionTokens) {
            this.minimumSectionTokens = minimumSectionTokens;
        }
    }
}
