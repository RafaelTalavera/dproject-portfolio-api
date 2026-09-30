ALTER TABLE portfolio.projects
    ADD COLUMN created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN created_by VARCHAR(100) NOT NULL DEFAULT 'system',
    ADD COLUMN updated_by VARCHAR(100) NOT NULL DEFAULT 'system';

CREATE TABLE portfolio.project_audit_events (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL,
    actor VARCHAR(100) NOT NULL,
    details TEXT NOT NULL
);

CREATE INDEX idx_project_audit_events_project_occurred
    ON portfolio.project_audit_events (project_id, occurred_at);
