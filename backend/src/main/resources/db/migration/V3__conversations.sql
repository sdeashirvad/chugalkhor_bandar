CREATE TABLE conversations (
    conversation_id VARCHAR(255) NOT NULL PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL,
    character_id VARCHAR(255) NOT NULL,
    character_display_name VARCHAR(255) NOT NULL,
    character_titles TEXT NOT NULL,
    character_species VARCHAR(255),
    character_home_territory VARCHAR(255),
    started_at TIMESTAMP NOT NULL,
    last_activity TIMESTAMP NOT NULL,
    status VARCHAR(32) NOT NULL
);

CREATE INDEX idx_conversations_session_id ON conversations (session_id);

CREATE TABLE conversation_messages (
    message_id VARCHAR(255) NOT NULL PRIMARY KEY,
    conversation_id VARCHAR(255) NOT NULL,
    sender VARCHAR(32) NOT NULL,
    message_timestamp TIMESTAMP NOT NULL,
    content TEXT NOT NULL,
    visibility VARCHAR(32) NOT NULL,
    metadata TEXT,
    sequence_order INTEGER NOT NULL,
    CONSTRAINT fk_conversation_messages_conversation
        FOREIGN KEY (conversation_id) REFERENCES conversations (conversation_id)
);

CREATE INDEX idx_conversation_messages_conversation_id ON conversation_messages (conversation_id);
