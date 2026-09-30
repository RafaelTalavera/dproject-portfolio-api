CREATE TABLE portfolio.users (
    id UUID PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_users_username UNIQUE (username)
);

INSERT INTO portfolio.users (id, username, password_hash, role)
VALUES (
    '7bdf18d0-e9d2-4bf9-9543-e8a072b08ca0',
    'portfolio.admin',
    '$2a$10$c7GeplQE7uUOwsUSELK66upK.StbY7zbhveup8p3Aeb/wlbMSCZu.',
    'ADMIN'
);
