USE `sysdb`;

DROP TABLE IF EXISTS `groups`;
CREATE TABLE `groups` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(1024) DEFAULT NULL,
  `createdBy` bigint(20) NOT NULL,
  `createdAt` datetime DEFAULT NULL,
  `ownedBy` bigint(20) NOT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  `modifiedBy` bigint(20) NOT NULL,
  `status` tinyint(4) NOT NULL,
  `parent` bigint(20) NOT NULL,
  `type` tinyint(4) NOT NULL,
  `maxMembers` bigint(20) DEFAULT 99999999,
  `appId` varchar(64) NOT NULL,
  `accountId`  bigint(20) NOT NULL,
  KEY `idx_ownedBy` (`ownedBy`),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP PROCEDURE IF EXISTS drop_group_memberships_sub_table;
DELIMITER $$
CREATE PROCEDURE drop_group_memberships_sub_table()
SQL SECURITY INVOKER
BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 0;
    WHILE i<100 DO
        SET aa=CONCAT('group_memberships_',i);
        SET @createobjfiles= CONCAT(' DROP TABLE IF EXISTS ',aa);
        PREPARE stmt FROM @createobjfiles;
        EXECUTE stmt;
        SET i=i+1;
    END WHILE;
END $$
DELIMITER ;
CALL  drop_group_memberships_sub_table;
DROP PROCEDURE IF EXISTS drop_group_memberships_sub_table;

DROP PROCEDURE IF EXISTS drop_group_memberships_r_sub_table;
DELIMITER $$
CREATE PROCEDURE drop_group_memberships_r_sub_table()
SQL SECURITY INVOKER
BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 0;
    WHILE i<100 DO
        SET aa=CONCAT('group_memberships_r_',i);
        SET @createobjfiles= CONCAT(' DROP TABLE IF EXISTS ',aa);
        PREPARE stmt FROM @createobjfiles;
        EXECUTE stmt;
        SET i=i+1;
    END WHILE;
END $$
DELIMITER ;
CALL  drop_group_memberships_r_sub_table;
DROP PROCEDURE IF EXISTS drop_group_memberships_r_sub_table;


DROP TABLE IF EXISTS `group_memberships_0`;
CREATE TABLE `group_memberships_0`(
	`id` bigint(20) NOT NULL,
	`groupId` bigint(20) NOT NULL,
	`name` varchar(255) DEFAULT NULL,
	`userId` bigint(20) NOT NULL,
	`userType` tinyint(4) NOT NULL DEFAULT 0,
	`username` varchar(255) DEFAULT NULL,
	`loginName` varchar(255) NOT NULL,
	`groupRole` tinyint(4) NOT NULL,
	PRIMARY KEY `memberships` (`groupId`,`userId`,`userType`),
	KEY `idx_groupId` (`groupId`),
 	KEY `idx_username` (`username`(64)),
 	KEY `idx_userId_type`  (`userId`,`userType`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `group_memberships_r_0`;
CREATE TABLE `group_memberships_r_0`(
	`id` bigint(20) NOT NULL,
	`groupId` bigint(20) NOT NULL,
	`name` varchar(255) DEFAULT NULL,
	`userId` bigint(20) NOT NULL,
	`userType` tinyint(4) DEFAULT 0,
	`username` varchar(255) DEFAULT NULL,
	`loginName` varchar(255) NOT NULL,
	`groupRole` tinyint(4) NOT NULL,
	KEY `idx_groupId_id` (`groupId`,`id`),
	KEY `idx_groupId` (`groupId`),
 	KEY `idx_username` (`username`(64)),
 	KEY `idx_userId_type`  (`userId`,`userType`),
	UNIQUE KEY `memberships_r` (`groupId`,`userId`,`userType`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP PROCEDURE IF EXISTS create_group_memberships_sub_table;
DELIMITER $$
CREATE PROCEDURE create_group_memberships_sub_table()
SQL SECURITY INVOKER
BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 1;
    WHILE i<100 DO
        SET aa=CONCAT('group_memberships_',i);
        SET @createobjfiles= CONCAT(' create table ',aa ,' like group_memberships_0');
        PREPARE stmt FROM @createobjfiles;
        EXECUTE stmt;
        SET i=i+1;
    END WHILE;
END $$
DELIMITER ;
CALL  create_group_memberships_sub_table;
DROP PROCEDURE IF EXISTS create_group_memberships_sub_table;

DROP PROCEDURE IF EXISTS create_group_memberships_r_sub_table;
DELIMITER $$
CREATE PROCEDURE create_group_memberships_r_sub_table()
SQL SECURITY INVOKER
BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 1;
    WHILE i<100 DO
        SET aa=CONCAT('group_memberships_r_',i);
        SET @createobjfiles= CONCAT(' create table ',aa ,' like group_memberships_r_0');
        PREPARE stmt FROM @createobjfiles;
        EXECUTE stmt;
        SET i=i+1;
    END WHILE;
END $$
DELIMITER ;
CALL  create_group_memberships_r_sub_table;
DROP PROCEDURE IF EXISTS create_group_memberships_r_sub_table;