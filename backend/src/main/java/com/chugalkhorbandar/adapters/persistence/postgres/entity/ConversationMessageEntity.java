package com.chugalkhorbandar.adapters.persistence.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "conversation_messages")
public class ConversationMessageEntity {

    @Id
    @Column(name = "message_id")
    private String messageId;

    @Column(name = "conversation_id", nullable = false)
    private String conversationId;

    @Column(nullable = false)
    private String sender;

    @Column(name = "message_timestamp", nullable = false)
    private Instant messageTimestamp;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String visibility;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    @Column(name = "sequence_order", nullable = false)
    private int sequenceOrder;

    public ConversationMessageEntity() {}

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Instant getMessageTimestamp() {
        return messageTimestamp;
    }

    public void setMessageTimestamp(Instant messageTimestamp) {
        this.messageTimestamp = messageTimestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public int getSequenceOrder() {
        return sequenceOrder;
    }

    public void setSequenceOrder(int sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
    }
}
