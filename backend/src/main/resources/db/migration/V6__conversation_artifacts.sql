CREATE TABLE conversation_artifacts (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    type VARCHAR(64) NOT NULL,
    owner_character_id VARCHAR(255) NOT NULL,
    recipient_character_id VARCHAR(255) NOT NULL,
    created_by_character_id VARCHAR(255) NOT NULL,
    conversation_id VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    summary TEXT NOT NULL,
    status VARCHAR(32) NOT NULL,
    priority VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    metadata_json TEXT NOT NULL,
    trace_json TEXT NOT NULL
);

CREATE INDEX idx_conversation_artifacts_owner ON conversation_artifacts (owner_character_id);
CREATE INDEX idx_conversation_artifacts_recipient ON conversation_artifacts (recipient_character_id);
CREATE INDEX idx_conversation_artifacts_conversation ON conversation_artifacts (conversation_id);
CREATE INDEX idx_conversation_artifacts_status ON conversation_artifacts (status);
