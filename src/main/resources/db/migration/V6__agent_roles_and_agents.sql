CREATE TABLE IF NOT EXISTS agent_roles (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    code TEXT NOT NULL UNIQUE,
    name TEXT NOT NULL UNIQUE,
    created_at INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS agents (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    code TEXT NOT NULL UNIQUE,
    name TEXT NOT NULL,
    role_id INTEGER NOT NULL,
    created_at INTEGER NOT NULL,
    FOREIGN KEY (role_id) REFERENCES agent_roles(id)
);

INSERT INTO agent_roles (code, name, created_at) VALUES
    ('ARCHITECT', 'Architect', strftime('%s','now') * 1000),
    ('MANAGER', 'Manager', strftime('%s','now') * 1000),
    ('DEVELOPER', 'Developer', strftime('%s','now') * 1000)
ON CONFLICT(code) DO NOTHING;
