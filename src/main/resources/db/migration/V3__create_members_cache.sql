CREATE TABLE portfolio.members (
    id UUID PRIMARY KEY,
    external_id VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    assignment VARCHAR(100) NOT NULL,
    synced_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_members_external_id UNIQUE (external_id)
);
