use userlogdb;
-- -----------
-- --From V1R3C10 to V1R5C00RC01-------
DROP PROCEDURE IF EXISTS create_admin_userlog_alter_table;
DELIMITER $$
CREATE PROCEDURE CREATE_admin_userlog_alter_table()
SQL SECURITY INVOKER
BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 0;
    WHILE i<100 DO
        SET aa=CONCAT('admin_userlog_',i);
        SET @createobjfiles= CONCAT(' CREATE TABLE ',aa,' (id varchar(128) NOT NULL,
  enterpriseId bigint(64) NOT NULL,
  createTime datetime NOT NULL,
  loginName varchar(255) NOT NULL,
  level int(6) NOT NULL,
  ip varchar(127) DEFAULT NULL,
  appId varchar(127) DEFAULT NULL,
  operatDescKey int(255) DEFAULT NULL,
  operatDesc varchar(1024) DEFAULT NULL,
  operatType varchar(255) DEFAULT NULL,
  operatLevel varchar(20) DEFAULT NULL,
  `authServerId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4');
        PREPARE stmt FROM @createobjfiles;
        EXECUTE stmt;
        SET i=i+1;
    END WHILE;
END $$
DELIMITER ;
CALL  create_admin_userlog_alter_table;


DROP TABLE IF EXISTS `system_login_log`;
CREATE TABLE `system_login_log` (
  `id` varchar(36) primary key,
  `loginName` varchar(255) NOT NULL,
  `createdAt` datetime NOT NULL,
  `clientType` smallint,
  `clientDeviceSN` varchar(127),
  `clientDeviceName` varchar(127),
  `clientAddress` varchar(128),
  `operateType` varchar(255) DEFAULT NULL,
  KEY `idx_loginName` (`loginName`),
  KEY `idx_createdAt` (`createdAt`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `user_login_log`;
CREATE TABLE `user_login_log` (
  `id` varchar(36) primary key,
  `userId` bigint(20) NOT NULL,
  `loginName` varchar(127),
  `createdAt` datetime NOT NULL,
  `clientType` smallint,
  `clientDeviceSN` varchar(127),
  `clientDeviceName` varchar(127),
  `clientAddress` varchar(64),
  `appId` varchar(64),
  KEY `idx_userId` (`userId`),
  KEY `idx_createdAt` (`createdAt`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;