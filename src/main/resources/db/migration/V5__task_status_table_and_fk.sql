CREATE TABLE IF NOT EXISTS task_status (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    code TEXT NOT NULL UNIQUE
);

INSERT INTO task_status (code) VALUES
    ('backlog'),
    ('planned'),
    ('in_progress'),
    ('done')
ON CONFLICT(code) DO NOTHING;

ALTER TABLE tasks ADD COLUMN task_status_id INTEGER REFERENCES task_status(id);

UPDATE tasks
SET task_status_id = (
    SELECT id FROM task_status WHERE code = CASE
        WHEN done = 1 THEN 'done'
        ELSE 'backlog'
    END
)
WHERE task_status_id IS NULL;

UPDATE tasks
SET task_status_id = (SELECT id FROM task_status WHERE code = 'backlog')
WHERE task_status_id IS NULL;
