-- Creating and using a database for further operations:
CREATE DATABASE clinic;
USE clinic;

-- Schema for the "patients" table:
CREATE TABLE patients (
    pat_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    age INT,
    village VARCHAR(100),
    phone VARCHAR(15)
);

-- Schema for the "visits" table:
CREATE TABLE visits (
    visit_id INT PRIMARY KEY AUTO_INCREMENT,
    pat_id INT,
    visit_date DATE DEFAULT (CURRENT_DATE),
    diagnosis VARCHAR(200),
    medicine VARCHAR(200),
    followup DATE,
    FOREIGN KEY (pat_id) REFERENCES patients(pat_id)
);
