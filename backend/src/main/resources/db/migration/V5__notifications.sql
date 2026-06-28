CREATE TABLE notifications (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    recipient_character_id VARCHAR(255) NOT NULL,
    type VARCHAR(64) NOT NULL,
    priority VARCHAR(32) NOT NULL,
    title VARCHAR(255) NOT NULL,
    summary TEXT NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    source VARCHAR(128) NOT NULL,
    trigger_name VARCHAR(128) NOT NULL,
    metadata_json TEXT NOT NULL
);

CREATE INDEX idx_notifications_recipient ON notifications (recipient_character_id);
CREATE INDEX idx_notifications_status ON notifications (status);
CREATE INDEX idx_notifications_created_at ON notifications (created_at);
