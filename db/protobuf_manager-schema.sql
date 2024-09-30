CREATE DATABASE IF NOT EXISTS `protobuf_manager`;

CREATE TABLE IF NOT EXISTS `group` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `name` CHAR(64) NOT NULL,
    `creator` CHAR(32) NULL,
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY name_uk (`name`)
);

CREATE TABLE IF NOT EXISTS `application` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `name` CHAR(64) NOT NULL,
    `zh_name` CHAR(64) NULL DEFAULT NULL,
    `intro` VARCHAR(256) NULL DEFAULT NULL,
    `group_id` INT NOT NULL,
    `group_name` CHAR(64) NOT NULL,
    `creator` CHAR(32) NULL,
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY full_name_uk (`name`, `group_name`)
);

CREATE TABLE IF NOT EXISTS `protobuf_info` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `name` CHAR(64) NOT NULL,
    `intro` VARCHAR(256) NULL DEFAULT NULL,
    `application_name` CHAR(64) NOT NULL,
    `group_name` CHAR(64) NOT NULL,
    `protocol` CHAR(16) NOT NULL DEFAULT 'grpc',
    `current_version` SMALLINT NOT NULL DEFAULT 0,
    `_next_version` SMALLINT NOT NULL DEFAULT 100,
    `creator` CHAR(32) NULL,
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY full_name_uk (`group_name`, `application_name`, `name`)
);

CREATE TABLE IF NOT EXISTS `protobuf_code` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `protobuf_id` INT NOT NULL,
    `protobuf_name` CHAR(64) NOT NULL,
    `code` TEXT NOT NULL,
    `version` SMALLINT NOT NULL DEFAULT 101,
    `creator` CHAR(32) NULL,
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

CREATE TABLE IF NOT EXISTS `test` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `name` CHAR(32) NOT NULL,
    `version` INT NOT NULL DEFAULT 1 ON UPDATE `version` + 1
);