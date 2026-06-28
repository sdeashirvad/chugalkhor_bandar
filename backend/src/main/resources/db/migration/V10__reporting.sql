CREATE TABLE report_archives (
    report_id VARCHAR(255) NOT NULL PRIMARY KEY,
    html_content TEXT NOT NULL,
    txt_content TEXT NOT NULL,
    json_content TEXT NOT NULL,
    markdown_content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE delivery_history (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    report_id VARCHAR(255) NOT NULL,
    recipient VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL,
    provider VARCHAR(64) NOT NULL,
    provider_message_id VARCHAR(255) NOT NULL,
    attempt INT NOT NULL,
    latency_ms BIGINT NOT NULL,
    error TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_delivery_history_report ON delivery_history (report_id);
CREATE INDEX idx_delivery_history_created ON delivery_history (created_at);
CREATE INDEX idx_delivery_history_status ON delivery_history (status);
