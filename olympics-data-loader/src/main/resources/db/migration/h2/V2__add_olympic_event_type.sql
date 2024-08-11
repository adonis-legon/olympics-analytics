CREATE TABLE olympic_event_type (
    id INTEGER PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT
);

INSERT INTO olympic_event_type (id, name, description) VALUES
    (1, 'Summer Olympic', 'Summer Olympic'),
    (2, 'Pan American', 'Pan American'),
    (3, 'Para Pan American', 'Para Pan American'),
    (4, 'Summer Paralympic', 'Summer Paralympic');