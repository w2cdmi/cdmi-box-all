USE `sysdb`;

--
-- Table structure for table `account`
--
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account`(
	`id` bigint(20) NOT NULL,
	`appId` varchar(64) NOT NULL,
	`domain` varchar(255) NOT NULL,
	`createdAt` datetime DEFAULT NULL,
	`modifiedAt` datetime DEFAULT NULL,
	`status` tinyint DEFAULT 0,
	`enterpriseId` bigint(20) DEFAULT NULL,
	`maxSpace` bigint DEFAULT 999999999999,
	`maxMember` integer DEFAULT 99999999,
	`maxFiles` bigint DEFAULT 99999999,
	`maxTeamspace` integer DEFAULT 99999999,
	`filePreviewable` bool DEFAULT false,
	`fileScanable` bool DEFAULT false,
	`currentMember` integer DEFAULT NULL,
	`currentTeamspace` integer DEFAULT NULL, 
	`currentSpace` bigint(20) DEFAULT NULL, 
	`currentFiles` bigint(20) DEFAULT NULL, 
	PRIMARY KEY `PK_Account`(`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `account_watermark`
--
drop table if exists account_watermark;
CREATE TABLE `account_watermark` (                            
    `accountId` bigint(20) NOT NULL,                     
    `watermark` mediumblob,                            
    `lastConfigTime` datetime NOT NULL,                         
    PRIMARY KEY (`accountId`)                                    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `account_accesskey`
--
DROP TABLE IF EXISTS `account_accesskey`;
CREATE TABLE `account_accesskey`(
	`id` varchar(128) NOT NULL,
	`secretKey` varchar(2048) NOT NULL,
	`secretKeyEncodeKey` varchar(2048) NOT NULL,
	`accountId` bigint NOT NULL,
	`createdAt` datetime DEFAULT NULL,
	PRIMARY KEY `PK_Account_Accesskey`(`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `user_config`
--
DROP TABLE IF EXISTS `user_config`;
CREATE TABLE `user_config`(
	`userId` BIGINT(20) NOT NULL,
	`name` VARCHAR(64) NOT NULL,
	`value` VARCHAR(255) NOT NULL,
  	KEY `userId` (`userId`),
	UNIQUE KEY `uq_userId_name`(`userId`,`name`)
)ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `user_0`
--

DROP TABLE IF EXISTS `user_0`;
CREATE TABLE `user_0` (
  `id` bigint(20) NOT NULL,
  `objectSid` varchar(255) DEFAULT NULL,
  `loginName` varchar(127) NOT NULL,
  `password` varchar(127) DEFAULT NULL,
  `name` varchar(127) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  `lastLoginAt` datetime DEFAULT NULL,
  `spaceUsed` bigint(20) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `domain` varchar(45) DEFAULT NULL,
  `spaceQuota` bigint(20) NOT NULL,
  `recycleDays` int(11) NOT NULL,
  `regionId` int(11) DEFAULT NULL,
  `appId` varchar(64) ,
  `accountId` bigint ,
  `type` tinyint(4) NOT NULL,
  `maxVersions` int(11) DEFAULT -1,
  `fileCount` bigint(20) DEFAULT 0,
  `lastStatisticsTime` datetime DEFAULT NULL,
  `securityId` int(11) DEFAULT 0,
  `versionFileSize` bigint NOT NULL DEFAULT -1,
  `versionFileType`  varchar(256) NULL,
  PRIMARY KEY (`id`),
  KEY `account_user_account_user` ( `id`, `accountId`),
  UNIQUE KEY `loginName_appId_UNIQUE` (`loginName`,`appId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP PROCEDURE IF EXISTS delete_user_sub_table;
DELIMITER $$
CREATE PROCEDURE delete_user_sub_table()
SQL SECURITY INVOKER
BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 1;
    WHILE i<100 DO
        SET aa=CONCAT('user_',i);
        SET @createobjfiles= CONCAT(' DROP TABLE IF EXISTS ',aa);
        PREPARE stmt FROM @createobjfiles;
        EXECUTE stmt;
        SET i=i+1;
    END WHILE;
END $$
DELIMITER ;
CALL  delete_user_sub_table;
DROP PROCEDURE IF EXISTS delete_user_sub_table;


DROP PROCEDURE IF EXISTS create_user_sub_table;
DELIMITER $$
CREATE PROCEDURE create_user_sub_table()
SQL SECURITY INVOKER
BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 1;
    WHILE i<100 DO
        SET aa=CONCAT('user_',i);
        SET @createobjfiles= CONCAT(' create table ',aa ,' like user_0');
        PREPARE stmt FROM @createobjfiles;
        EXECUTE stmt;
        SET i=i+1;
    END WHILE;
END $$
DELIMITER ;
CALL  create_user_sub_table;
DROP PROCEDURE IF EXISTS create_user_sub_table;

--
-- Table structure for table `accountId_user_0`
--

DROP TABLE IF EXISTS `account_user_0`;
CREATE TABLE `account_user_0` (
  `id` bigint(20) NOT NULL,
  `objectSid` varchar(255) DEFAULT NULL,
  `loginName` varchar(127) NOT NULL,
  `password` varchar(127) DEFAULT NULL,
  `name` varchar(127) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  `lastLoginAt` datetime DEFAULT NULL,
  `spaceUsed` bigint(20) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `domain` varchar(45) DEFAULT NULL,
  `spaceQuota` bigint(20) NOT NULL,
  `recycleDays` int(11) NOT NULL,
  `regionId` int(11) DEFAULT NULL,
  `appId` varchar(64) ,
  `accountId` bigint ,
  `type` tinyint(4) NOT NULL,
  `maxVersions` int(11) DEFAULT -1,
  `fileCount` bigint(20) DEFAULT 0,
  `lastStatisticsTime` datetime DEFAULT NULL,
  `securityId` int(11) DEFAULT 0,
  `versionFileSize` bigint NOT NULL DEFAULT -1,
  `versionFileType`  varchar(256) NULL,
  PRIMARY KEY (`id`),
  KEY `account_user_accountId` (`accountId`),
  UNIQUE KEY `loginName_appId_UNIQUE` (`loginName`,`appId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP PROCEDURE IF EXISTS delete_account_user_sub_table;
DELIMITER $$
CREATE PROCEDURE delete_account_user_sub_table()
SQL SECURITY INVOKER
BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 1;
    WHILE i<100 DO
        SET aa=CONCAT('account_user_',i);
        SET @createobjfiles= CONCAT(' DROP TABLE IF EXISTS ',aa);
        PREPARE stmt FROM @createobjfiles;
        EXECUTE stmt;
        SET i=i+1;
    END WHILE;
END $$
DELIMITER ;
CALL  delete_account_user_sub_table;
DROP PROCEDURE IF EXISTS delete_account_user_sub_table;


DROP PROCEDURE IF EXISTS create_account_user_sub_table;
DELIMITER $$
CREATE PROCEDURE create_account_user_sub_table()
SQL SECURITY INVOKER
BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 1;
    WHILE i<100 DO
        SET aa=CONCAT('account_user_',i);
        SET @createobjfiles= CONCAT(' create table ',aa ,' like account_user_0');
        PREPARE stmt FROM @createobjfiles;
        EXECUTE stmt;
        SET i=i+1;
    END WHILE;
END $$
DELIMITER ;
CALL  create_account_user_sub_table;
DROP PROCEDURE IF EXISTS create_account_user_sub_table;

drop table if exists node_statistics_day;
CREATE TABLE node_statistics_day (
   `day` int NOT NULL,
   `appId` varchar(64) NOT NULL,
   `regionId` 	int(11) NOT NULL,
   `fileCount`    bigint(20) NULL,
   `trashFileCount`    bigint(20) NULL,
   `deletedFileCount`    bigint(20) NULL,
   `spaceUsed`    bigint(20) NULL,
   `trashSpaceUsed`    bigint(20) NULL,
   `deletedSpaceUsed`    bigint(20) NULL,
   `addedFileCount`    bigint(20) NULL,
   `addedTrashFileCount`    bigint(20) NULL,
   `addedDeletedFileCount`    bigint(20) NULL,
   `addedSpaceUsed`    bigint(20) NULL,
   `addedTrashSpaceUsed`    bigint(20) NULL,
   `addedDeletedSpaceUsed`    bigint(20) NULL,
   primary key (`day`,`appId`,`regionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

drop table if exists obj_statistics_day;
CREATE TABLE obj_statistics_day (
   `day` int NOT NULL,
   `regionId` 	int(11) NOT NULL,
   `fileCount`    bigint(20) NULL,
   `actualFileCount`    bigint(20) NULL,
   `spaceUsed`    bigint(20) NULL,
   `actualSpaceUsed`    bigint(20) NULL,
   `addedFileCount`    bigint(20) NULL,
   `addedActualFileCount`    bigint(20) NULL,
   `addedSpaceUsed`    bigint(20) NULL,
   `addedActualSpaceUsed`    bigint(20) NULL,
   primary key (`day`,`regionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

drop table if exists temp_obj_statistics_day;
CREATE TABLE temp_obj_statistics_day (
   `day` int NOT NULL,
   `regionId` 	int(11) NOT NULL,
   `resourceGroupId` 	int(11) NOT NULL,
   `dbName` varchar(64) NOT NULL,
   `fileCount`    bigint(20) NULL,
   `actualFileCount`    bigint(20) NULL,
   `spaceUsed`    bigint(20) NULL,
   `actualSpaceUsed`    bigint(20) NULL,
   `addedFileCount`    bigint(20) NULL,
   `addedActualFileCount`    bigint(20) NULL,
   `addedSpaceUsed`    bigint(20) NULL,
   `addedActualSpaceUsed`    bigint(20) NULL,
   primary key (`day`,`dbName`,`resourceGroupId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- 
-- Date: 2015/04/03
-- Description: Statistics AK / SK record table
-- Database:sysdb
--
DROP TABLE IF EXISTS `statistics_accesskey`;
CREATE TABLE `statistics_accesskey`(
	`id` varchar(128) NOT NULL,
	`secretKey` varchar(2048) NOT NULL,
	`secretKeyEncodeKey` varchar(2048) NOT NULL,
	`createdAt` datetime DEFAULT NULL,
    `secretKeyCryptKey` varchar(2048) DEFAULT '',
	PRIMARY KEY `PK_Statistics_accesskey`(`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


DROP table if exists user_statistics_day;
CREATE TABLE user_statistics_day (
   `day` int NOT NULL,
   `appId` varchar(64) NOT NULL,
   `regionId` 	int(11) NOT NULL,   
   `userCount`    bigint(20) NULL,
   `addedUserCount`    bigint(20) NULL,
   primary key (`day`,`appId`,`regionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

drop table if exists temp_node_user_statistics_day;
CREATE TABLE temp_node_user_statistics_day (
   `day` int NOT NULL,
   `appId` varchar(64) NULL,
   `dbName` varchar(64) NOT NULL,
   `accountId` bigint(20) NULL,
   `regionId` 	int(11) NULL,
   `ownedBy` BIGINT(20) NOT NULL, 
   `resourceGroupId`    bigint(20) NOT NULL,
   `fileCount`    bigint(20) NULL,
   `trashFileCount`    bigint(20) NULL,
   `deletedFileCount`    bigint(20) NULL,
   `spaceUsed`    bigint(20) NULL,
   `trashSpaceUsed`    bigint(20) NULL,
   `deletedSpaceUsed`    bigint(20) NULL,
   primary key (`day`,`ownedBy`, `dbName`, `resourceGroupId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

drop table if exists sys_conc_statistics_day;
CREATE TABLE sys_conc_statistics_day (
   `day` int NOT NULL,
   `maxUpload` smallint DEFAULT 0,
   `maxDownload` smallint DEFAULT 0,
   primary key (`day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

drop table if exists temp_conc_statistics_day;
CREATE TABLE temp_conc_statistics_day (
   `day` int NOT NULL,
   `timeUnit` smallint NOT NULL,
   `host` varchar(64) NOT NULL,
   `maxUpload` smallint DEFAULT 0,
   `maxDownload` smallint DEFAULT 0,
   primary key (`day`,`timeUnit`,`host`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


drop table if exists security_scan_task;
CREATE TABLE security_scan_task (
   `taskId` varchar(32) NOT NULL,
   `objectId` varchar(32) NOT NULL,
   `nodeId` bigint(20) NOT NULL,
   `nodeName` varchar(255) NULL, 
   `ownedBy` bigint(20) NOT NULL, 
   `dssId` int(11) NOT NULL, 
   `createdAt` datetime NOT NULL,
   `modifiedAt` datetime NOT NULL,
   `status` tinyint(4) NOT NULL COMMENT '1. Wait for implementation; 2. being implemented; 3. Implementation success; -1 execution exception',
   `priority` int(11) DEFAULT 5 COMMENT 'Scan task priority, with 1-9, 9: the highest priority, 1: the lowest priority',
   PRIMARY KEY (`taskId`),
   KEY `INDEX_OBJECT_ID` (`objectId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- create zookeeperInfo table--
DROP TABLE IF EXISTS `zookeeperInfo`;
CREATE TABLE `zookeeperInfo` (
  `ip` VARCHAR(128) NOT NULL,
  `port` VARCHAR(10) NOT NULL,
  PRIMARY KEY (`ip`,`port`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;


USE `sysdb`;

DROP TABLE IF EXISTS `copy_task`;
CREATE TABLE `copy_task` (
  `taskId` varchar(32) NOT NULL,
  `srcOwnedBy` bigint(20) DEFAULT NULL,
  `srcINodeId` bigint(20) DEFAULT NULL,
  `srcObjectId` varchar(32) NOT NULL,
  `fileName` varchar(255) DEFAULT NULL,
  `destOwnedBy` bigint(20) DEFAULT NULL,
  `destINodeId` bigint(20) DEFAULT NULL,
  `destObjectId` varchar(32) NOT NULL,
  `size` bigint(20) DEFAULT NULL,
  `copyType` int(11) DEFAULT NULL COMMENT '1: disaster recovery, 10: nearby access',
  `exeType` int(11) DEFAULT NULL COMMENT '0: Immediate, 1: Timing',
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  `isPop` tinyint(1) DEFAULT '0',
  `exeStartAt` varchar(32) DEFAULT NULL,
  `exeEndAt` varchar(32) DEFAULT NULL,
  `policyId` int(11) DEFAULT NULL,
  `srcRegionId` int(11) DEFAULT '-1',
  `srcResourceGroupId` int(11) DEFAULT '-1',
  `destRegionId` int(11) DEFAULT '-1',
  `destResourceGroupId` int(11) DEFAULT '-1',
  `state` int(11) DEFAULT '0',
  `priority` int(11) DEFAULT '0' COMMENT '0: normal, 1: Advanced',
  `exeResult` int(11) DEFAULT '0',
  PRIMARY KEY (`taskId`),
  KEY `EXE_TIME` (`exeType`,`exeStartAt`),
  KEY `IDX_DEST_OBJECTID` (`destObjectId`),
  KEY `IDX_SRC_OBJECTID` (`srcObjectId`),
  KEY `STATE_EXERESULT` (`state`,`exeResult`),
  KEY `STATE_PRI` (`state`,`priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


DROP TABLE IF EXISTS `copy_policy`;
CREATE TABLE `copy_policy` (
  `id` int(11) NOT NULL,
  `name` varchar(128) DEFAULT NULL,
  `description` varchar(512) DEFAULT NULL,
  `appId` varchar(32) DEFAULT NULL,
  `type` int(11) DEFAULT '0' COMMENT '0: Full APP data replication, 1: Some users copy',
  `copyType` int(11) DEFAULT '1' COMMENT '1: disaster recovery, 10: nearby access',
  `exeType` int(11) DEFAULT '0' COMMENT 'Execution type, 0 timely implementation, 1 scheduled',
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  `state` int(11) DEFAULT '0' COMMENT'0: normal, 1: Pause',
  `exeStartAt` varchar(32) DEFAULT NULL,
  `exeEndAt` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `APP` (`appId`),
  KEY `NAME` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


DROP TABLE IF EXISTS `copy_policy_site_info`;
CREATE TABLE `copy_policy_site_info` (
  `id` int(11) NOT NULL,
  `policyId` int(11) NOT NULL,
  `srcRegionId` int(11) NOT NULL ,
  `srcResourceGroupId` int(11) NOT NULL,
  `destRegionId` int(11) NOT NULL,
  `destResourceGroupId` int(11) NOT NULL,
  `state` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `POLICYID` (`policyId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `copy_policy_user_config`;
CREATE TABLE `copy_policy_user_config` (
  `policyId` int(11) NOT NULL,
  `userType` int(11) NOT NULL ,
  `userId` bigint(20) NOT NULL,
  PRIMARY KEY (`policyId`,`userType`, `userId`),
  KEY `userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



DROP TABLE IF EXISTS `region_network_distance`;
CREATE TABLE `region_network_distance` (
  `name` varchar(128) NOT NULL,
  `srcRegionId` int(11) NOT NULL ,
  `srcResourceGroupId` int(11) NOT NULL,
  `destRegionId` int(11) NOT NULL,
  `destResourceGroupId` int(11) NOT NULL,
  `value` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`name`),
  UNIQUE KEY `idx_distance` (`srcRegionId`,`srcResourceGroupId`,`destRegionId`,`destResourceGroupId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


DROP PROCEDURE IF EXISTS drop_user_mirror_statistic_info_sub_table;
DELIMITER $$
CREATE PROCEDURE drop_user_mirror_statistic_info_sub_table()
SQL SECURITY INVOKER
BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 0;
    WHILE i<100 DO
        SET aa=CONCAT('user_mirror_statistic_info_',i);
        SET @dropobjfiles= CONCAT(' DROP TABLE IF EXISTS ',aa);
        PREPARE stmt FROM @dropobjfiles;
        EXECUTE stmt;
        SET i=i+1;
    END WHILE;
END $$
DELIMITER ;
CALL  drop_user_mirror_statistic_info_sub_table;
DROP PROCEDURE IF EXISTS drop_user_mirror_statistic_info_sub_table;

DROP TABLE IF EXISTS `user_mirror_statistic_info_0`;
CREATE TABLE `user_mirror_statistic_info_0` (
  `id` varchar(32) NOT NULL,
  `appId` varchar(32) NOT NULL,
  `accountId` bigint(20) NOT NULL,
  `userId` bigint(20) NOT NULL,
  `policyId` int(11) NOT NULL,
  `statistcDate` datetime NOT NULL,
  `mirrorFileNumber` bigint(20) NOT NULL,
  `notMirrorFileNumber` bigint(20) NOT NULL,
  `mirrorFileInfoStr` text DEFAULT NULL,
  `notMirrorFileInfoStr` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `accountId` (`accountId`),
  KEY `userId` (`userId`),
  KEY `policyId` (`policyId`),
  KEY `user_policy` (`userId`,`policyId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP PROCEDURE IF EXISTS create_user_mirror_statistic_info_sub_table;
DELIMITER $$
CREATE PROCEDURE create_user_mirror_statistic_info_sub_table()
sql security invoker
BEGIN
    declare aa varchar(256);
    declare i int default 1;
    while i<100 do
        set aa=CONCAT('user_mirror_statistic_info_',i);
        set @createobjfiles= CONCAT(' create table ',aa ,' like user_mirror_statistic_info_0');
        prepare stmt from @createobjfiles;
        execute stmt;
        set i=i+1;
    end while;
END $$
DELIMITER ;
CALL  create_user_mirror_statistic_info_sub_table;
DROP PROCEDURE IF EXISTS create_user_mirror_statistic_info_sub_table;


use sysdb;		
DROP TABLE IF EXISTS `service_node`;
CREATE TABLE `service_node` (
  `regionId` int(11) DEFAULT NULL,
  `dcId` int(11) DEFAULT NULL,
  `clusterId` int(11) NOT NULL,
  `clusterType` varchar(32) NOT NULL,
  `clusterName` varchar(127) DEFAULT NULL,
  `name` varchar(64) NOT NULL,
  `managerIp` varchar(64) NOT NULL,
  `managerPort` varchar(32) NOT NULL,
  `innerAddr` varchar(127) NOT NULL,
  `innerPort` varchar(32) NOT NULL,
  `serviceAddr` varchar(127) DEFAULT NULL,
  `natAddr` varchar(64) DEFAULT NULL,
  `natPath` varchar(64) DEFAULT NULL,
  `checkPath` varchar(45) DEFAULT NULL,
  `status` int(11) DEFAULT '0' NOT NULL,
  `runStatus` int(11) DEFAULT '0' NOT NULL,
  `runStatusUpdateTime` bigint(20) DEFAULT '0',
  `runStatusDesc` text DEFAULT NULL ,
  `priority` int(11) DEFAULT '1',
  PRIMARY KEY `service_node` (`clusterId`,`clusterType`,`name`),
  UNIQUE KEY `manager_ip_port` (`managerIp`,`managerPort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



 
DROP TABLE IF EXISTS `files_add`;
CREATE TABLE `files_add`(
	`ownedBy` bigint(20) NOT NULL,
	`nodeId` bigint(20) NOT NULL,
	`accountId` bigint(20) NOT NULL,
	`size` bigint(20) DEFAULT NULL,  
	PRIMARY KEY (`OwnedBy`,`nodeId`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE INDEX UserIndex ON `files_add` (`OwnedBy`);

 
DROP TABLE IF EXISTS `files_delete`;
CREATE TABLE `files_delete`(
	`ownedBy` bigint(20) NOT NULL,
	`nodeId` bigint(20) NOT NULL,
	`accountId` bigint(20) NOT NULL,
	`size` bigint(20) DEFAULT NULL,  
	PRIMARY KEY (`OwnedBy`,`nodeId`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE INDEX UserIndex ON `files_delete` (`OwnedBy`);

DROP TABLE IF EXISTS `temporary_userinfo`;
CREATE TABLE `temporary_userinfo`(
	`ownedBy` bigint(20) NOT NULL,
	`accountId` bigint(20) NOT NULL,
	`spaceUsed` bigint(20) DEFAULT NULL, 
	`spaceChanged` bigint(20) DEFAULT NULL, 
	`createdAt` datetime NOT NULL,
	`currentFileCount` bigint(20) DEFAULT NULL, 
	`changedFileCount` bigint(20) DEFAULT NULL, 
	PRIMARY KEY (`OwnedBy`,`createdAt`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `clear_recyclebin_record`;
CREATE TABLE `clear_recyclebin_record`( 
	`ownedBy` bigint(20) NOT NULL,  
	`accountId` bigint(20) NOT NULL,
    `createdAt` datetime NOT NULL,
	PRIMARY KEY (`OwnedBy`,`createdAt`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


DROP TABLE IF EXISTS `user_data_migration_task`;
CREATE TABLE `user_data_migration_task`( 
	`cloudUserId` bigint(20) NOT NULL,  
    `createdAt` datetime DEFAULT NULL,
    `modifiedAt` datetime DEFAULT NULL,
    `defaultRegionId` int(11) DEFAULT NULL,
    `destRegionId` int(11) DEFAULT NULL,
    `destResourceGroupId` int(11) DEFAULT NULL,
    `status` int(11) DEFAULT '0' NOT NULL comment 'INIT:0,EXECUTE_SCAN:1,EXECUTE_MIGRATION:2,COMPELETE:3,FAILED:4',
    `exeAgent` varchar(32) DEFAULT NULL,
    `totalFiles` bigint(20) DEFAULT '0' NOT NULL,  
    `totalSizes` bigint(20) DEFAULT '0' NOT NULL,  
    `curFiles` bigint(20) DEFAULT '0' NOT NULL,  
    `curSizes` bigint(20) DEFAULT '0' NOT NULL,  
	PRIMARY KEY (`cloudUserId`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `history_data_copy_process_info`;
CREATE TABLE `history_data_copy_process_info` (
`id`  varchar(32) NOT NULL ,
`createdAt`  datetime NOT NULL ,
`policyId`  int(11) NOT NULL ,
`status`  int(11) NOT NULL ,
`totalFiles`  bigint(20) NOT NULL DEFAULT 0 ,
`totalSizes`  bigint(20) NOT NULL DEFAULT 0 ,
`curFiles`  bigint(20) NOT NULL DEFAULT 0 ,
`curSizes`  bigint(20) NOT NULL DEFAULT 0 ,
`failedFiles`  bigint(20) NOT NULL DEFAULT 0 ,
`failedSizes`  bigint(20) NOT NULL DEFAULT 0 ,
`modifiedAt`  datetime NOT NULL ,
`endTime`  datetime NULL DEFAULT NULL ,
PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `history_data_copy_process_info_everyday`;
CREATE TABLE `history_data_copy_process_info_everyday` (
`id`  varchar(32) NOT NULL ,
`parentId`  varchar(32) NOT NULL ,
`policyId`  int(11) NOT NULL ,
`startTime`  datetime NOT NULL ,
`endTime`  datetime NULL DEFAULT NULL ,
`newAddFiles`  bigint(20) NOT NULL DEFAULT 0,
`newAddSizes`  bigint(20) NOT NULL DEFAULT 0,
PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `never_copy_object`;
CREATE TABLE `never_copy_object` (
`id`  varchar(32) NOT NULL ,
`parentId`  varchar(32) NOT NULL ,
`policyId`  int(11) NOT NULL ,
`appId`  varchar(32) NOT NULL ,
`ownedBy`  bigint(20) NOT NULL ,
`nodeId`  bigint(20) NOT NULL ,
`fileName`  varchar(256) NOT NULL ,
`objectId`  varchar(32) NOT NULL ,
`size`  bigint(20) NOT NULL ,
`md5`  varchar(256) NULL DEFAULT NULL ,
`blockMD5`  varchar(256) NULL DEFAULT NULL ,
`reason`  varchar(512) DEFAULT NULL ,
PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `time_config`;
CREATE TABLE `time_config` (
  `uuid` varchar(36) NOT NULL ,
  `createdAt` datetime DEFAULT NULL,
  `exeStartAt` varchar(32) DEFAULT NULL,
  `exeEndAt` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `table_scan_break_info`;
CREATE TABLE `table_scan_break_info` (
`sysTaskId`  varchar(64) NOT NULL,
`limitOffset`  bigint(10) NOT NULL,
`model`  varchar(10) NULL,
`length`  integer(8) NULL,
`breakTime`  date NULL,
`outPut`  varchar(300) NULL,
  PRIMARY KEY (`sysTaskId`) 
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


DROP TABLE IF EXISTS `history_data_copy_process_info`;
CREATE TABLE `history_data_copy_process_info` (
`id`  varchar(32) NOT NULL ,
`createdAt`  datetime NOT NULL ,
`policyId`  int(11) NOT NULL ,
`status`  int(11) NOT NULL ,
`totalFiles`  bigint(20) NOT NULL DEFAULT 0 ,
`totalSizes`  bigint(20) NOT NULL DEFAULT 0 ,
`curFiles`  bigint(20) NOT NULL DEFAULT 0 ,
`curSizes`  bigint(20) NOT NULL DEFAULT 0 ,
`failedFiles`  bigint(20) NOT NULL DEFAULT 0 ,
`failedSizes`  bigint(20) NOT NULL DEFAULT 0 ,
`modifiedAt`  datetime NOT NULL ,
`endTime`  datetime NULL DEFAULT NULL ,
PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `history_data_copy_process_info_everyday`;
CREATE TABLE `history_data_copy_process_info_everyday` (
`id`  varchar(32) NOT NULL ,
`parentId`  varchar(32) NOT NULL ,
`policyId`  int(11) NOT NULL ,
`startTime`  datetime NOT NULL ,
`endTime`  datetime NULL DEFAULT NULL ,
`newAddFiles`  bigint(20) NOT NULL DEFAULT 0,
`newAddSizes`  bigint(20) NOT NULL DEFAULT 0,
PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `never_copy_object`;
CREATE TABLE `never_copy_object` (
`id`  varchar(32) NOT NULL ,
`parentId`  varchar(32) NOT NULL ,
`policyId`  int(11) NOT NULL ,
`appId`  varchar(32) NOT NULL ,
`ownedBy`  bigint(20) NOT NULL ,
`nodeId`  bigint(20) NOT NULL ,
`fileName`  varchar(256) NOT NULL ,
`objectId`  varchar(32) NOT NULL ,
`size`  bigint(20) NOT NULL ,
`md5`  varchar(256) NULL DEFAULT NULL ,
`blockMD5`  varchar(256) NULL DEFAULT NULL ,
`reason`  varchar(512) DEFAULT NULL ,
PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


DROP TABLE IF EXISTS `time_config`;
CREATE TABLE `time_config` (
  `uuid` varchar(36) NOT NULL ,
  `createdAt` datetime DEFAULT NULL,
  `exeStartAt` varchar(32) DEFAULT NULL,
  `exeEndAt` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;


DROP TABLE IF EXISTS `table_scan_break_info`;
CREATE TABLE `table_scan_break_info` (
`sysTaskId`  varchar(64) NOT NULL,
`limitOffset`  bigint(10) NOT NULL,
`model`  varchar(10) NULL,
`length`  integer(8) NULL,
`breakTime`  date NULL,
`outPut`  varchar(300) NULL,
  PRIMARY KEY (`sysTaskId`) 
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


DROP TABLE IF EXISTS `doctype_user_config`;

CREATE TABLE `doctype_user_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `value` varchar(4000) NOT NULL,
  `isDefault` int(20) NOT NULL,
  `userId` bigint(20) NOT NULL,
  `appId` varchar(64) DEFAULT '-1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

delete from doctype_user_config where id=1;
insert  into `doctype_user_config`(`id`,`name`,`value`,`isDefault`,`userId`,`appId`) values (1,'ufm.DocType.document','txt,doc,docx,ppt,pptx,pdf,xls,xlsx,inf,ini,chm,css,dat,dot,dic,exc,log,rtf,wps',1,-1,'-1');
delete from doctype_user_config where id=2;
insert  into `doctype_user_config`(`id`,`name`,`value`,`isDefault`,`userId`,`appId`) values (2,'ufm.DocType.picture','psd,png,bmp,gif,ico,img,jpeg,jpg,mag,svg,tga,tiff',1,-1,'-1');
delete from doctype_user_config where id=3;
insert  into `doctype_user_config`(`id`,`name`,`value`,`isDefault`,`userId`,`appId`) values (3,'ufm.DocType.audio','mp3,wma,wav,ra,cd,md,aif,aifc,aiff,au,cmf,snd,svx,voc',1,-1,'-1');
delete from doctype_user_config where id=4;
insert  into `doctype_user_config`(`id`,`name`,`value`,`isDefault`,`userId`,`appId`) values (4,'ufm.DocType.video','mp4,rmvb,rm,asf,avi,mov,movie,mpg,swf',1,-1,'-1');
delete from doctype_user_config where id=5;
insert  into `doctype_user_config`(`id`,`name`,`value`,`isDefault`,`userId`,`appId`) values (5,'ufm.DocType.other','',1,-1,'-1');