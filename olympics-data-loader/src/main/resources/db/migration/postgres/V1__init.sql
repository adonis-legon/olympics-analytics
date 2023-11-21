CREATE TABLE olympic_event (
    id SERIAL PRIMARY KEY,
    event_type INTEGER,
    host_city VARCHAR(255),
    start_date DATE,
    end_date DATE
);

CREATE TABLE participant_country (
    id SERIAL PRIMARY KEY,
    rank INTEGER,
    name VARCHAR(255),
    gold_medals INTEGER,
    silver_medals INTEGER,
    bronze_medals INTEGER,
    olympic_event_id INTEGER REFERENCES olympic_event(id)
);