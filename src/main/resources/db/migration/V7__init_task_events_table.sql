DROP TABLE IF EXISTS task_events;
CREATE TABLE task_events (
    id INT PRIMARY KEY AUTO_INCREMENT,
    task_id INT,
    name VARCHAR(30),
    occurrence DATETIME
)