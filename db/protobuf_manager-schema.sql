CREATE DATABASE IF NOT EXISTS `protobuf_manager`;

CREATE TABLE IF NOT EXISTS `group` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `name` CHAR(64) NOT NULL,
    `intro` VARCHAR(256) NULL DEFAULT '',
    `creator` CHAR(32) NOT NULL,
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY name_uk (`name`)
);

CREATE TABLE IF NOT EXISTS `application` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `name` CHAR(64) NOT NULL,
    `intro` VARCHAR(256) NULL DEFAULT '',
    `group_id` INT NOT NULL,
    `creator` CHAR(32) NOT NULL,
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY name_uk (`name`, `group_id`)
);

CREATE TABLE IF NOT EXISTS `protobuf` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `application_id` INT NOT NULL,
    `name` CHAR(64) NOT NULL,
    `intro` VARCHAR(256) NULL DEFAULT '',
    `protocol` CHAR(16) NOT NULL DEFAULT 'grpc',
    `current_version` SMALLINT NOT NULL DEFAULT 0,
    `creator` CHAR(32) NOT NULL,
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY name_uk (`name`, `application_id`)
);

CREATE TABLE IF NOT EXISTS `protobuf_code` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `protobuf_id` INT NOT NULL,
    `code` TEXT NOT NULL,
    `version` SMALLINT NOT NULL DEFAULT 101,
    `creator` CHAR(32) NOT NULL,
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY version_uk (`protobuf_id`, `version`)
);

CREATE TABLE IF NOT EXISTS `group_owner` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `group_id` INT NOT NULL,
    `owner` CHAR(32) NOT NULL,
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY name_uk (`group_id`, `owner`)
);

CREATE TABLE IF NOT EXISTS `applaction_owner` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `application_id` INT NOT NULL,
    `owner` CHAR(32) NOT NULL,
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY name_uk (`application_id`, `owner`)
);

CREATE TABLE IF NOT EXISTS `IAM` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `user` CHAR(32) NOT NULL,
    `resource_type` CHAR(16) NOT NULL,
    `resource_id` INT NOT NULL,
    `permission` TINYINT NOT NULL
);