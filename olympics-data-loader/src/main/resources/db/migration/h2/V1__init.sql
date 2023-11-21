CREATE TABLE olympic_event (
    id INT AUTO_INCREMENT PRIMARY KEY,
    event_type INT,
    host_city VARCHAR(255),
    start_date DATE,
    end_date DATE
);

CREATE TABLE participant_country (
    id INT AUTO_INCREMENT PRIMARY KEY,
    rank INT,
    name VARCHAR(255),
    gold_medals INT,
    silver_medals INT,
    bronze_medals INT,
    olympic_event_id INT,
    FOREIGN KEY (olympic_event_id) REFERENCES olympic_event(id)
);