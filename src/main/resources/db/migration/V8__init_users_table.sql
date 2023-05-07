DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(30) NOT NULL,
    password VARCHAR(60) NOT NULL,
    first_name VARCHAR(30),
    last_name VARCHAR(30)
);