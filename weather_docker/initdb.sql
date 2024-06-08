-- liquibase formatted sql

-- changeset V1tali1ty:1

SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0;
SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0;
SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE =
        'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema new_schema
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `new_schema` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `new_schema`;

-- -----------------------------------------------------
-- Table `city`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `city`
(
    `id`      BIGINT       NOT NULL AUTO_INCREMENT,
    `country` VARCHAR(255) NULL DEFAULT NULL,
    `lat`     DOUBLE       NOT NULL,
    `lng`     DOUBLE       NOT NULL,
    `name`    VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE
    )
    ENGINE = InnoDB
    AUTO_INCREMENT = 3
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `client`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `client`
(
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `address`    VARCHAR(255) NOT NULL,
    `date_added` DATE         NULL DEFAULT (CURRENT_DATE),
    `name`       VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `address_UNIQUE` (`address` ASC) VISIBLE,
    UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE
    )
    ENGINE = InnoDB
    AUTO_INCREMENT = 3
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `role`
(
    `id`   BIGINT       NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE,
    UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE
    )
    ENGINE = InnoDB
    AUTO_INCREMENT = 3
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `customer`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `customer`
(
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `date_added` DATE         NULL DEFAULT (CURRENT_DATE),
    `name`       VARCHAR(255) NOT NULL,
    `password`   VARCHAR(255) NOT NULL,
    `client_id`  BIGINT       NOT NULL,
    `role_id`    BIGINT       NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `FK8axppsuewb9gg1dhc4cj07qjf` (`client_id` ASC) VISIBLE,
    INDEX `FKo2oh87rk6lunf0lic1svc9y75` (`role_id` ASC) VISIBLE,
    UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
    UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE,
    CONSTRAINT `FK8axppsuewb9gg1dhc4cj07qjf`
    FOREIGN KEY (`client_id`)
    REFERENCES `client` (`id`)
    on delete cascade ,
    CONSTRAINT `FKo2oh87rk6lunf0lic1svc9y75`
    FOREIGN KEY (`role_id`)
    REFERENCES `role` (`id`)
    )
    ENGINE = InnoDB
    AUTO_INCREMENT = 3
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `forecast_type`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `forecast_type`
(
    `id`   BIGINT       NOT NULL AUTO_INCREMENT,
    `type` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `type_UNIQUE` (`type` ASC) VISIBLE,
    UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE
    )
    ENGINE = InnoDB
    AUTO_INCREMENT = 3
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `provider`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `provider`
(
    `id`                BIGINT       NOT NULL AUTO_INCREMENT,
    `credential`        VARCHAR(255) NOT NULL,
    `is_active`         BIT(1)       NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
    UNIQUE INDEX `credential_UNIQUE` (`credential` ASC) VISIBLE
    )
    ENGINE = InnoDB
    AUTO_INCREMENT = 3
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `forecast`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `forecast`
(
    `id`               BIGINT       NOT NULL AUTO_INCREMENT,
    `conditions`       VARCHAR(255) NULL DEFAULT NULL,
    `humidity`         DOUBLE       NULL DEFAULT NULL,
    `pressure`         DOUBLE       NULL DEFAULT NULL,
    `temperature`      DOUBLE       NULL DEFAULT NULL,
    `time`             DATETIME     NULL DEFAULT NULL,
    `time_stamp`       DATETIME     NULL DEFAULT NULL,
    `wind_direction`   DOUBLE       NULL DEFAULT NULL,
    `wind_speed`       DOUBLE       NULL DEFAULT NULL,
    `city_id`          BIGINT       NOT NULL,
    `forecast_type_id` BIGINT       NOT NULL,
    `provider_id`      BIGINT       NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `FKmj3x8wvpsdj7u7xqklmx1vrab` (`city_id` ASC) VISIBLE,
    INDEX `FK74repupfi9fh3epn5tsn4hh7x` (`forecast_type_id` ASC) VISIBLE,
    INDEX `FKl2peyh45gmkch7329d2gdquwo` (`provider_id` ASC) VISIBLE,
    UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
    CONSTRAINT `FK74repupfi9fh3epn5tsn4hh7x`
    FOREIGN KEY (`forecast_type_id`)
    REFERENCES `forecast_type` (`id`),
    CONSTRAINT `FKl2peyh45gmkch7329d2gdquwo`
    FOREIGN KEY (`provider_id`)
    REFERENCES `provider` (`id`),
    CONSTRAINT `FKmj3x8wvpsdj7u7xqklmx1vrab`
    FOREIGN KEY (`city_id`)
    REFERENCES `city` (`id`)
    )
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `forecast_sequence`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `forecast_sequence`
(
    `next_val` BIGINT NULL DEFAULT NULL
)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `refresh_token`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `refresh_token`
(
    `id`          BIGINT      NOT NULL AUTO_INCREMENT,
    `credential`  BINARY(255) NOT NULL,
    `customer_id` BIGINT      NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `FKthlbvd34s3un1d4pxxqv2ni6c` (`customer_id` ASC) VISIBLE,
    UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
    CONSTRAINT `FKthlbvd34s3un1d4pxxqv2ni6c`
    FOREIGN KEY (`customer_id`)
    REFERENCES `customer` (`id`)
    )
    ENGINE = InnoDB
    AUTO_INCREMENT = 8
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `response`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `response`
(
    `id`               BIGINT     NOT NULL AUTO_INCREMENT,
    `data`             MEDIUMTEXT NULL DEFAULT NULL,
    `time_stamp`       DATETIME   NULL DEFAULT (CURRENT_TIMESTAMP),
    `city_id`          BIGINT     NOT NULL,
    `forecast_type_id` BIGINT     NOT NULL,
    `provider_id`      BIGINT     NOT NULL,
    `is_updated`      BOOLEAN     NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `FK1nhceg3sm1srlvx95gfjro9x9` (`city_id` ASC) VISIBLE,
    INDEX `FKhrie6of4yj0y549bsxsuiyelb` (`forecast_type_id` ASC) VISIBLE,
    INDEX `FKdvc2twrc0d24anm7tvgft6aj9` (`provider_id` ASC) VISIBLE,
    UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
    CONSTRAINT `FK1nhceg3sm1srlvx95gfjro9x9`
    FOREIGN KEY (`city_id`)
    REFERENCES `city` (`id`),
    CONSTRAINT `FKdvc2twrc0d24anm7tvgft6aj9`
    FOREIGN KEY (`provider_id`)
    REFERENCES `provider` (`id`),
    CONSTRAINT `FKhrie6of4yj0y549bsxsuiyelb`
    FOREIGN KEY (`forecast_type_id`)
    REFERENCES `forecast_type` (`id`)
    )
    ENGINE = InnoDB
    AUTO_INCREMENT = 33
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;

SET SQL_MODE = @OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS;
