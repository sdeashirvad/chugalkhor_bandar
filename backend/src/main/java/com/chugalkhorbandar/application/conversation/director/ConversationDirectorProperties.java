package com.chugalkhorbandar.application.conversation.director;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "chugalkhor.conversation-director")
public class ConversationDirectorProperties {

    private int maxMessages = 3;
    private double devDelayMultiplier = 0.25;
    private ConversationEnergyThresholds conversationEnergyThresholds = new ConversationEnergyThresholds();
    private MessageDelayRange messageDelayRange = new MessageDelayRange();

    public int getMaxMessages() {
        return maxMessages;
    }

    public void setMaxMessages(int maxMessages) {
        this.maxMessages = maxMessages;
    }

    public double getDevDelayMultiplier() {
        return devDelayMultiplier;
    }

    public void setDevDelayMultiplier(double devDelayMultiplier) {
        this.devDelayMultiplier = devDelayMultiplier;
    }

    public ConversationEnergyThresholds getConversationEnergyThresholds() {
        return conversationEnergyThresholds;
    }

    public void setConversationEnergyThresholds(ConversationEnergyThresholds conversationEnergyThresholds) {
        this.conversationEnergyThresholds = conversationEnergyThresholds;
    }

    public MessageDelayRange getMessageDelayRange() {
        return messageDelayRange;
    }

    public void setMessageDelayRange(MessageDelayRange messageDelayRange) {
        this.messageDelayRange = messageDelayRange;
    }

    public static class ConversationEnergyThresholds {

        private int mediumSecondMessageModulo = 3;
        private int highThirdMessageModulo = 4;

        public int getMediumSecondMessageModulo() {
            return mediumSecondMessageModulo;
        }

        public void setMediumSecondMessageModulo(int mediumSecondMessageModulo) {
            this.mediumSecondMessageModulo = mediumSecondMessageModulo;
        }

        public int getHighThirdMessageModulo() {
            return highThirdMessageModulo;
        }

        public void setHighThirdMessageModulo(int highThirdMessageModulo) {
            this.highThirdMessageModulo = highThirdMessageModulo;
        }
    }

    public static class MessageDelayRange {

        private long secondMessageMinMs = 2000;
        private long secondMessageMaxMs = 4000;
        private long thirdMessageMinMs = 3000;
        private long thirdMessageMaxMs = 6000;

        public long getSecondMessageMinMs() {
            return secondMessageMinMs;
        }

        public void setSecondMessageMinMs(long secondMessageMinMs) {
            this.secondMessageMinMs = secondMessageMinMs;
        }

        public long getSecondMessageMaxMs() {
            return secondMessageMaxMs;
        }

        public void setSecondMessageMaxMs(long secondMessageMaxMs) {
            this.secondMessageMaxMs = secondMessageMaxMs;
        }

        public long getThirdMessageMinMs() {
            return thirdMessageMinMs;
        }

        public void setThirdMessageMinMs(long thirdMessageMinMs) {
            this.thirdMessageMinMs = thirdMessageMinMs;
        }

        public long getThirdMessageMaxMs() {
            return thirdMessageMaxMs;
        }

        public void setThirdMessageMaxMs(long thirdMessageMaxMs) {
            this.thirdMessageMaxMs = thirdMessageMaxMs;
        }
    }
}
