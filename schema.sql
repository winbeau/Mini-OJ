CREATE DATABASE IF NOT EXISTS minioj
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE minioj;

CREATE TABLE IF NOT EXISTS users (
    id       INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64)  NOT NULL UNIQUE,
    pwd_hash VARCHAR(128) NOT NULL
);

CREATE TABLE IF NOT EXISTS problems (
    id             INT PRIMARY KEY,
    title          VARCHAR(256) NOT NULL,
    judge_class    VARCHAR(256) NOT NULL,
    time_limit_ms  BIGINT NOT NULL DEFAULT 2000,
    mem_limit_mb   INT    NOT NULL DEFAULT 256
);

CREATE TABLE IF NOT EXISTS submissions (
    id           INT PRIMARY KEY AUTO_INCREMENT,
    problem_id   INT         NOT NULL,
    username     VARCHAR(64) NOT NULL,
    lang         VARCHAR(16) NOT NULL,
    status       VARCHAR(8)  NOT NULL,
    passed       INT         NOT NULL DEFAULT 0,
    total        INT         NOT NULL DEFAULT 0,
    time_ms      BIGINT      NOT NULL DEFAULT 0,
    src_path     VARCHAR(512),
    submitted_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (problem_id) REFERENCES problems(id)
);
