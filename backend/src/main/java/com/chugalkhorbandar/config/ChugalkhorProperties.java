package com.chugalkhorbandar.config;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "chugalkhor")
public class ChugalkhorProperties {

    private String name = "Chugalkhor Bandar";
    private String bootstrapFolder = "../bootstrap";
    private String chronicleFolder = "../chronicles";
    private SessionProperties session = new SessionProperties();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBootstrapFolder() {
        return bootstrapFolder;
    }

    public void setBootstrapFolder(String bootstrapFolder) {
        this.bootstrapFolder = bootstrapFolder;
    }

    public String getChronicleFolder() {
        return chronicleFolder;
    }

    public void setChronicleFolder(String chronicleFolder) {
        this.chronicleFolder = chronicleFolder;
    }

    public SessionProperties getSession() {
        return session;
    }

    public void setSession(SessionProperties session) {
        this.session = session;
    }

    public static class SessionProperties {
        private String defaultPasskey = "jungle";
        private int inactivityMinutes = 30;
        private Map<String, String> passkeys = new LinkedHashMap<>();

        public String getDefaultPasskey() {
            return defaultPasskey;
        }

        public void setDefaultPasskey(String defaultPasskey) {
            this.defaultPasskey = defaultPasskey;
        }

        public int getInactivityMinutes() {
            return inactivityMinutes;
        }

        public void setInactivityMinutes(int inactivityMinutes) {
            this.inactivityMinutes = inactivityMinutes;
        }

        public Map<String, String> getPasskeys() {
            return passkeys;
        }

        public void setPasskeys(Map<String, String> passkeys) {
            this.passkeys = passkeys;
        }
    }
}
