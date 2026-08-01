CREATE TABLE users (
    id UUID PRIMARY KEY,

    full_name VARCHAR(128) NOT NULL,

    email VARCHAR(256) NOT NULL UNIQUE,

    password_hash VARCHAR(128) NOT NULL,

    role VARCHAR(64) NOT NULL,

    status VARCHAR(32) NOT NULL,

    avatar_url VARCHAR(256),

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    updated_at TIMESTAMP NULL,

    last_login_at TIMESTAMP NULL
);

CREATE INDEX idx_users_email ON users(email);