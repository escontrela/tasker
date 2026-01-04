CREATE TABLE IF NOT EXISTS projects (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    name TEXT NOT NULL,
    created_at INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS priorities (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    code TEXT NOT NULL UNIQUE,
    description TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS tags (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    project_id INTEGER NOT NULL,
    name TEXT NOT NULL,
    UNIQUE (project_id, name),
    FOREIGN KEY (project_id) REFERENCES projects(id)
);

CREATE TABLE IF NOT EXISTS tasks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    project_id INTEGER NOT NULL,
    priority_id INTEGER NOT NULL,
    tag_id INTEGER,
    external_code TEXT,
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    start_at INTEGER,
    end_at INTEGER,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL,
    FOREIGN KEY (project_id) REFERENCES projects(id),
    FOREIGN KEY (priority_id) REFERENCES priorities(id),
    FOREIGN KEY (tag_id) REFERENCES tags(id)
);

INSERT INTO users (email, created_at)
VALUES ('default@tasker.local', strftime('%s','now') * 1000)
ON CONFLICT(email) DO NOTHING;

INSERT INTO projects (user_id, name, created_at)
VALUES ((SELECT id FROM users WHERE email = 'default@tasker.local'), 'Default project', strftime('%s','now') * 1000)
ON CONFLICT(name) DO NOTHING;

INSERT INTO priorities (code, description) VALUES
    ('HIGH', 'High priority'),
    ('MEDIUM', 'Medium priority'),
    ('LOW', 'Low priority')
ON CONFLICT(code) DO NOTHING;

INSERT INTO tags (project_id, name)
VALUES ((SELECT id FROM projects WHERE name = 'Default project'), 'General')
ON CONFLICT(project_id, name) DO NOTHING;
