CREATE DATABASE IF NOT EXISTS controldb;

USE controldb;

CREATE TABLE users
(
    userid     VARCHAR(64) NOT NULL PRIMARY KEY,
    firstname  VARCHAR(64),
    lastname   VARCHAR(64),
    salt       VARCHAR(64) NOT NULL,
    password   VARCHAR(64) NOT NULL,
    isLocked   TINYINT NOT NULL DEFAULT 0,
    faillogin  TINYINT NOT NULL,
    otpsecret  VARCHAR(64) NOT NULL,
    label      TINYINT NOT NULL
);

CREATE TABLE news
(
    id         INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    userid     VARCHAR(64) NOT NULL,
    content    TEXT,
    date       TIMESTAMP NOT NULL,
    label      TINYINT NOT NULL
);