/**
 * 鉴权服务
 */
CREATE DATABASE IF NOT EXISTS `auth`;

CREATE TABLE IF NOT EXISTS `roles` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `name` CHAR(32) NOT NULL,
    `description` VARCHAR(256) NULL,
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_name (`name`)
);

CREATE TABLE IF NOT EXISTS `resources` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `name` CHAR(32) NOT NULL,
    `description` VARCHAR(256) NULL,
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_name (`name`)
);

CREATE TABLE IF NOT EXISTS `users_roles` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `user` CHAR(64) NOT NULL,
    `role_id` INT NOT NULL,
    UNIQUE KEY uk_name (`user`, `role_id`)
);

/**Perms: Query=1 Create=2 Edit=4 Remove=8, can mix */
CREATE TABLE IF NOT EXISTS `roles_perms` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `role_id` CHAR(32) NOT NULL,
    `resource_id` CHAR(32) NOT NULL,
    `perms` INT NOT NULL DEFAULT 0,
    `context` VARCHAR(256) NULL DEFAULT NULL,
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
);