CREATE DATABASE IF NOT EXISTS `logdb`;
USE `logdb`;

drop table if exists db_version;
CREATE TABLE `db_version` (                            
    `version` varchar(128) NOT NULL,                     
    `modified` date NOT NULL,                            
    `description` varchar(2048) NOT NULL,                
    `sequence` int(11) NOT NULL AUTO_INCREMENT,          
    PRIMARY KEY (`version`),
    KEY `sequence` (`sequence`)                                     
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;
insert into db_version(version, modified, description) values('1.3.00.2901', '2014-9-25', '');


DROP TABLE IF EXISTS `user_log`;
CREATE TABLE `user_log` (
  `id` varchar(36) primary key,
  `userId` bigint(20) NOT NULL,
  `loginName` varchar(127),
  `type` int NOT NULL,
  `createdAt` datetime NOT NULL,
  `clientType` smallint,
  `clientDeviceSN` varchar(127),
  `clientDeviceName` varchar(127),
  `clientAddress` varchar(64),
  `clientOS` varchar(64),
  `clientVersion` varchar(127),
  `appId` varchar(64),
  `level` smallint,
  `keyword` varchar(255),
  `detail` varchar(3072),
  KEY `idx_userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


DROP TABLE IF EXISTS `admin_log_0`;
CREATE TABLE `admin_log_0` (
  `id` varchar(36) primary key,
  `userId` bigint(20) NOT NULL,
  `loginName` varchar(127),
  `type` int NOT NULL,
  `createdAt` datetime NOT NULL,
  `clientType` smallint,
  `clientDeviceSN` varchar(127),
  `clientDeviceName` varchar(127),
  `clientAddress` varchar(64),
  `clientOS` varchar(64),
  `clientVersion` varchar(127),
  `appId` varchar(64),
  `level` smallint,
  `keyword` varchar(255),
  `detail` varchar(255),
  KEY `idx_userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;