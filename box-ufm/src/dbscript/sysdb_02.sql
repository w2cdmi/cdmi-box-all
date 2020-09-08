USE `sysdb`;

--
-- Table structure for table `teamspace`
--
DROP TABLE IF EXISTS `teamspace`;
CREATE TABLE `teamspace` (
  `cloudUserId` BIGINT(20) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `type` int(11) default 0,
  `description` TEXT,
  `ownerBy` BIGINT(20) NOT NULL,
  `createdBy` BIGINT(20) NOT NULL,
  `createdAt` DATETIME DEFAULT NULL,
  `modifiedBy` BIGINT(20) DEFAULT NULL,
  `modifiedAt` DATETIME DEFAULT NULL,
  `status` INT(11) DEFAULT NULL,
  `maxMembers` INT(11) DEFAULT -1,
  `appId` VARCHAR(64) DEFAULT NULL,
  `uploadNotice` TINYINT(1) DEFAULT '1' COMMENT 'Whether the team space to add file to send messages, 0: sent, 1: does not transmit',
  `accountId` bigint(20) default -1,
  KEY `idx_name_appId` (`name`(64),`appId`,`accountId`),
  PRIMARY KEY (`cloudUserId`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;



DROP PROCEDURE IF EXISTS drop_teamspace_memberships_sub_table;
DELIMITER $$
CREATE PROCEDURE drop_teamspace_memberships_sub_table()
SQL SECURITY INVOKER
BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 0;
    WHILE i<50 DO
        SET aa=CONCAT('teamspace_memberships_',i);
        SET @createobjfiles= CONCAT(' DROP TABLE IF EXISTS ',aa);
        PREPARE stmt FROM @createobjfiles;
        EXECUTE stmt;
        SET i=i+1;
    END WHILE;
END $$
DELIMITER ;
CALL  drop_teamspace_memberships_sub_table;
DROP PROCEDURE IF EXISTS drop_teamspace_memberships_sub_table;

DROP PROCEDURE IF EXISTS drop_teamspace_memberships_user_sub_table;
DELIMITER $$
CREATE PROCEDURE drop_teamspace_memberships_user_sub_table()
SQL SECURITY INVOKER
BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 0;
    WHILE i<50 DO
        SET aa=CONCAT('teamspace_memberships_user_',i);
        SET @createobjfiles= CONCAT(' DROP TABLE IF EXISTS ',aa);
        PREPARE stmt FROM @createobjfiles;
        EXECUTE stmt;
        SET i=i+1;
    END WHILE;
END $$
DELIMITER ;
CALL  drop_teamspace_memberships_user_sub_table;
DROP PROCEDURE IF EXISTS drop_teamspace_memberships_user_sub_table;

--
-- Table structure for table `teamspace_memberships`
--
DROP TABLE IF EXISTS `teamspace_memberships_0`;
CREATE TABLE `teamspace_memberships_0` (
  `id` BIGINT(20) NOT NULL,
  `cloudUserId` BIGINT(20) NOT NULL,
  `userId` BIGINT(20) NOT NULL,
  `userType` VARCHAR(32) NOT NULL,
  `username` VARCHAR(255) NOT NULL,
  `loginName` VARCHAR(255) NOT NULL,
  `teamRole` VARCHAR(255) NOT NULL,
  `createdBy` BIGINT(20) NOT NULL,
  `createdAt` DATETIME DEFAULT NULL,
  `modifiedBy` BIGINT(20) DEFAULT NULL,
  `modifiedAt` DATETIME DEFAULT NULL,
  `status` INT(11) DEFAULT NULL,
  KEY `idx_cloudUserID_id` (`cloudUserId`,`id`),
  UNIQUE KEY `memberships` (`cloudUserId`,`userId`,`userType`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `teamspace_memberships_r`
--
DROP TABLE IF EXISTS `teamspace_memberships_user_0`;
CREATE TABLE `teamspace_memberships_user_0` (
  `id` BIGINT(20) NOT NULL,
  `cloudUserId` BIGINT(20) NOT NULL,
  `userId` BIGINT(20) NOT NULL,
  `userType` VARCHAR(32) NOT NULL,
  `username` VARCHAR(255) NOT NULL,
  `loginName` VARCHAR(255) NOT NULL,
  `teamRole` VARCHAR(255) NOT NULL,
  `createdBy` BIGINT(20) NOT NULL,
  `createdAt` DATETIME DEFAULT NULL,
  `modifiedBy` BIGINT(20) DEFAULT NULL,
  `modifiedAt` DATETIME DEFAULT NULL,
  `status` INT(11) DEFAULT NULL,
  UNIQUE KEY `memberships` (`cloudUserId`,`userId`,`userType`),
  KEY `idx_team_r_user` (`userId`,`userType`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;





DROP PROCEDURE IF EXISTS create_teamspace_memberships_sub_table;
DELIMITER $$
CREATE PROCEDURE create_teamspace_memberships_sub_table()
SQL SECURITY INVOKER
BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 1;
    WHILE i<50 DO
        SET aa=CONCAT('teamspace_memberships_',i);
        SET @createobjfiles= CONCAT(' create table ',aa ,' like teamspace_memberships_0');
        PREPARE stmt FROM @createobjfiles;
        EXECUTE stmt;
        SET i=i+1;
    END WHILE;
END $$
DELIMITER ;
CALL  create_teamspace_memberships_sub_table;
DROP PROCEDURE IF EXISTS create_teamspace_memberships_sub_table;

DROP PROCEDURE IF EXISTS create_teamspace_memberships_user_sub_table;
DELIMITER $$
CREATE PROCEDURE create_teamspace_memberships_user_sub_table()
SQL SECURITY INVOKER
BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 1;
    WHILE i<50 DO
        SET aa=CONCAT('teamspace_memberships_user_',i);
        SET @createobjfiles= CONCAT(' create table ',aa ,' like teamspace_memberships_user_0');
        PREPARE stmt FROM @createobjfiles;
        EXECUTE stmt;
        SET i=i+1;
    END WHILE;
END $$
DELIMITER ;
CALL  create_teamspace_memberships_user_sub_table;
DROP PROCEDURE IF EXISTS create_teamspace_memberships_user_sub_table;


--
-- Table structure for table `teamspace_role`
--
DROP TABLE IF EXISTS `teamspace_role`;
CREATE TABLE `teamspace_role` (
  `teamRole` VARCHAR(128) NOT NULL,
  `acl`  INT(11) DEFAULT NULL,
  `description` TEXT,
   PRIMARY KEY (`teamRole`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;
INSERT INTO teamspace_role(teamRole,acl,description) VALUES ("admin",2,"owner");
INSERT INTO teamspace_role(teamRole,acl,description)VALUES ("manager",1,"admin");
INSERT INTO teamspace_role(teamRole,acl,description) VALUES ("member",0,"member");

-- Table structure for table `Resource_Role`
--
DROP TABLE IF EXISTS `resource_role`;
CREATE TABLE `resource_role` (
  `resourceRole` VARCHAR(128) NOT NULL,
  `acl`  BIGINT(20) NOT NULL,
  `createdBy` BIGINT(20) NOT NULL,
  `createdAt` DATETIME DEFAULT NULL,
  `modifiedBy` BIGINT(20) DEFAULT NULL,
  `modifiedAt` DATETIME DEFAULT NULL,
  `description` TEXT,
  PRIMARY KEY (`resourceRole`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

INSERT INTO `resource_role` VALUES ('auther', '11111111', '0', null, '0', null, 'auther');
INSERT INTO `resource_role` VALUES ('downLoader', '1001101', '0', null, '0', null, 'downLoader');
INSERT INTO `resource_role` VALUES ('editor', '1111111', '0', null, '0', null, 'editor');
INSERT INTO `resource_role` VALUES ('lister', '1', '0', null, '0', null, 'lister');
INSERT INTO `resource_role` VALUES ('previewer', '1001', '0', null, '0', null, 'previewer');
INSERT INTO `resource_role` VALUES ('prohibitVisitors', '0', '0', null, '0', null, 'prohibitVisitors');
INSERT INTO `resource_role` VALUES ('uploadAndView', '1001111', '0', null, '0', null, 'uploadAndView');
INSERT INTO `resource_role` VALUES ('uploader', '1011', '0', null, '0', null, 'uploader');
INSERT INTO `resource_role` VALUES ('viewer', '1001101', '0', null, '0', null, 'viewer');

DROP TABLE IF EXISTS `license_file`;
CREATE TABLE `license_file` (
  `id` VARCHAR(64) NOT NULL,
  `createdAt` DATETIME NOT NULL,
  `createdBy` int NOT NULL,
  `name` varchar(255),
  `status`SMALLINT,
  `content` blob,
  `sha1` VARCHAR(60) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `license_node`;
CREATE TABLE `license_node` (
  `id` VARCHAR(64) NOT NULL,
  `lastModified` DATETIME NOT NULL,
  `esn` VARCHAR(127),
  `serverType` SMALLINT,
  `status` SMALLINT,
  `port` INT,
  `nodeAddress` VARCHAR(127),
  `licenseId` VARCHAR(127),
  `name` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `systask`;
CREATE TABLE `systask` (
  `taskID` varchar(64) NOT NULL COMMENT 'task ID',
  `pTaskID` varchar(64) DEFAULT NULL COMMENT 'parent task ID',
  `state` tinyint(8) NOT NULL DEFAULT '0' COMMENT 'Task status, 0 initialization, 1 Executive, 2 for complete, 3 represents the exception',
  `createTime` datetime NOT NULL COMMENT 'create Time',
  `exeAgent` varchar(256) DEFAULT NULL COMMENT 'execute agent time',
  `taskInfo` text COMMENT 'Task specific information, transmit data using JSON',
  `exeUpdateTime` datetime DEFAULT NULL COMMENT 'Task Update time, used to determine whether the task timeout',
  `exeRuningInfo` text COMMENT 'Task running process information, easy to do task Recovery',
  `taskKey` varchar(512) NOT NULL COMMENT 'Tasks keywords represent each type of task',
  `TimeOut` bigint(20) DEFAULT NULL COMMENT 'Task timeout',
  PRIMARY KEY (`taskID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `userdb_info`;
CREATE TABLE `userdb_info` (
  `dbName` varchar(255) NOT NULL COMMENT 'The database name',
  `dbBeanName` varchar(255) DEFAULT NULL COMMENT 'beanname of database',
  `dbNumber` int(10) NOT NULL COMMENT 'used for separating database',
   PRIMARY KEY (`dbName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*insert  into `userdb_info`(`dbName`,`dbBeanName`,`dbNumber`) values ('userdb_1','userdb1',1),('userdb_2','userdb2',2),('userdb_3','userdb3',3),('userdb_4','userdb4',4),('userdb_5','userdb5',5),('userdb_6','userdb6',6),('userdb_7','userdb7',7),('userdb_8','userdb8',8);
*/
insert  into `userdb_info`(`dbName`,`dbBeanName`,`dbNumber`) values ('userdb_0','userdb',1);


/*ת�������*/
DROP TABLE IF EXISTS `convert_task`;
CREATE TABLE `convert_task` (
  `taskId` varchar(64) NOT NULL,
  `objectId` varchar(64) NOT NULL,
  `imageObjectId` varchar(64) NOT NULL,
  `owneId` int(11) NOT NULL,
  `fileName` varchar(255) NOT NULL,
  `level` int(11) DEFAULT '1',
  `status` int(11) DEFAULT '1',
  `percent` int(11) DEFAULT '0',
  `retryCount` int(11) DEFAULT '0',
  `fileModifyTime` datetime DEFAULT NULL,
  `convertTime` datetime DEFAULT NULL,
  `resourceGroupId` varchar(10) DEFAULT NULL,
  `csIp` varchar(20) DEFAULT NULL,
  `convertBeginTime` datetime DEFAULT NULL,
  `convertEndTime` datetime DEFAULT NULL,
  `bigFileFlag` int(11) DEFAULT '0',
  `destFileFlag` int(11) DEFAULT '0',
  PRIMARY KEY (`taskId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*ת������ɹ���ʷ��*/
DROP TABLE IF EXISTS `convert_task_history`;
CREATE TABLE `convert_task_history` (
  `taskId` varchar(64) NOT NULL,
  `objectId` varchar(64) NOT NULL,
  `imageObjectId` varchar(64) NOT NULL,
  `owneId` int(11) NOT NULL,
  `fileName` varchar(255) NOT NULL,
  `level` int(11) DEFAULT '1',
  `status` int(11) DEFAULT '1',
  `percent` int(11) DEFAULT '0',
  `retryCount` int(11) DEFAULT '0',
  `fileModifyTime` datetime DEFAULT NULL,
  `convertTime` datetime DEFAULT NULL,
  `resourceGroupId` varchar(10) DEFAULT NULL,
  `csIp` varchar(20) DEFAULT NULL,
  `convertBeginTime` datetime DEFAULT NULL,
  `convertEndTime` datetime DEFAULT NULL,
  `bigFileFlag` int(11) DEFAULT '0',
  `destFileFlag` int(11) DEFAULT '0',
  PRIMARY KEY (`taskId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*������ļ��Ľڵ� ע��*/
DROP TABLE IF EXISTS `nodeSign`;
CREATE TABLE `nodeSign` (
  `resourceGroupId` varchar(10) NOT NULL,
  `csIp` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`resourceGroupId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*image��Ϣ��*/
DROP TABLE IF EXISTS `image_object`;
CREATE TABLE `image_object` (
  `sourceObjectId` varchar(64) NOT NULL,
  `accountId` varchar(64) DEFAULT '',
  `imageObjectId` varchar(64) NOT NULL,
  `totalPages` int(11) DEFAULT '0',
  `pageIndex` text,
  `resourceGroupId` varchar(32) NOT NULL,
  `convertTime` datetime DEFAULT NULL,
  PRIMARY KEY (`sourceObjectId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*�Ŷӿռ����Ա�*/
/*����*/
DROP TABLE IF EXISTS `teamspace_attribute`;
CREATE TABLE `teamspace_attribute` (
  `attributeId` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '���Ա�����',
  `cloudUserId` bigint(20) NOT NULL COMMENT '������Ŷӿռ������',
  `name` varchar(255) NOT NULL COMMENT '������������atuoPreview(�Զ�Ԥ�� value��0:�ر�,1:����), priority(ת�����ȼ� value��0:��,1:��,2:��)',
  `value` varchar(255) NOT NULL COMMENT '����ֵ: ',
  PRIMARY KEY (`attributeId`),
  KEY `idx_cloudUserId_name` (`cloudUserId`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*������*/
insert into teamspace_attribute(cloudUserId, name, value) select teamspace.cloudUserId, 'autoPreview', '0' from teamspace;
insert into teamspace_attribute(cloudUserId, name, value) select teamspace.cloudUserId, 'priority', '1' from teamspace;



ALTER TABLE convert_task ADD inodeId INT(20);
ALTER TABLE convert_task_history ADD inodeId INT(20);

ALTER TABLE convert_task DROP PRIMARY KEY;
ALTER TABLE convert_task ADD PRIMARY KEY(objectid);

ALTER TABLE convert_task_history DROP PRIMARY KEY;
ALTER TABLE convert_task_history ADD PRIMARY KEY(objectid);