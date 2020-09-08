CREATE DATABASE  IF NOT EXISTS `ds_fileobjectdb`;
USE `ds_fileobjectdb`;

drop table if exists db_version;
CREATE TABLE `db_version` (                            
    `version` varchar(128) NOT NULL,                     
    `modified` date NOT NULL,                            
    `description` varchar(2048) NOT NULL,                
    `sequence` int(11) NOT NULL AUTO_INCREMENT,          
    PRIMARY KEY (`version`),
    KEY `sequence` (`sequence`)                                     
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;
insert into db_version(version, modified, description) values('1.3.00.2901', '2014-9-11', '');

--
-- Table structure for table `fileobject_0`
--
DROP TABLE IF EXISTS `fileobject_0`;
CREATE TABLE `fileobject_0` (
  `objectid` varchar(64) NOT NULL,
  `path` varchar(2048) NOT NULL,
  `sha1` varchar(128) DEFAULT NULL,
  `length` BIGINT(20) DEFAULT 0,
  `state` int(11) DEFAULT NULL,
  PRIMARY KEY (`objectid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
DROP PROCEDURE IF EXISTS create_fileobject_sub_table;
DELIMITER $$
CREATE PROCEDURE create_fileobject_sub_table()
sql security invoker
BEGIN
    declare aa varchar(256);
    declare i int default 1;
    while i<100 do
        set aa=CONCAT('fileobject_',i);
        set @droptable= CONCAT(' drop table IF EXISTS ', aa);
        set @createtable= CONCAT(' create table ',aa ,' like fileobject_0');
        prepare stmt from @droptable;
        execute stmt;
        prepare stmt from @createtable;
        execute stmt;
        set i=i+1;
    end while;
END $$
DELIMITER ;
CALL  create_fileobject_sub_table;
DROP PROCEDURE IF EXISTS create_fileobject_sub_table;

--
-- Table structure for table `fileobject_delete_task_0`
--
DROP TABLE IF EXISTS `fileobject_delete_task_0`;
CREATE TABLE `fileobject_delete_task_0` (
  `objectid` varchar(64) NOT NULL,
  `path` varchar(2048) NOT NULL,
  `sha1` varchar(128) DEFAULT NULL,
  `length` BIGINT(20) DEFAULT 0,
  `state` int(11) DEFAULT NULL,
  `deleteAt` datetime DEFAULT NULL,
  `deleteTimes` int(11) DEFAULT 0,
  `modified` datetime DEFAULT NULL,  
  PRIMARY KEY (`objectid`),
  KEY (`state`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
DROP PROCEDURE IF EXISTS create_fileobject_delete_task_sub_table;
DELIMITER $$
CREATE PROCEDURE create_fileobject_delete_task_sub_table()
sql security invoker
BEGIN
    declare aa varchar(256);
    declare i int default 1;
    while i<10 do
        set aa=CONCAT('fileobject_delete_task_',i);
        set @droptable= CONCAT(' drop table IF EXISTS ', aa);
        set @createtable= CONCAT(' create table ',aa ,' like fileobject_delete_task_0');
        prepare stmt from @droptable;
        execute stmt;
        prepare stmt from @createtable;
        execute stmt;
        set i=i+1;
    end while;
END $$
DELIMITER ;
CALL create_fileobject_delete_task_sub_table;
DROP PROCEDURE IF EXISTS create_fileobject_delete_task_sub_table;

--
-- Table structure for table `fileobject_delete_log_0`
--
DROP TABLE IF EXISTS `fileobject_delete_log_0`;
CREATE TABLE `fileobject_delete_log_0` (
  `objectid` varchar(64) NOT NULL,
  `path` varchar(2048) NOT NULL,
  `sha1` varchar(128) DEFAULT NULL,
  `length` BIGINT(20) DEFAULT 0,
  `state` int(11) DEFAULT NULL,
  `deleteAt` datetime DEFAULT NULL,
  `deleteTimes` int(11) DEFAULT 0,
  `modified` datetime DEFAULT NULL,  
  PRIMARY KEY (`objectid`),
  KEY (`state`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
DROP PROCEDURE IF EXISTS create_fileobject_delete_log_sub_table;
DELIMITER $$
CREATE PROCEDURE create_fileobject_delete_log_sub_table()
sql security invoker
BEGIN
    declare aa varchar(256);
    declare i int default 1;
    while i<20 do
        set aa=CONCAT('fileobject_delete_log_',i);
        set @droptable= CONCAT(' drop table IF EXISTS ', aa);
        set @createtable= CONCAT(' create table ',aa ,' like fileobject_delete_log_0');
        prepare stmt from @droptable;
        execute stmt;
        prepare stmt from @createtable;
        execute stmt;
        set i=i+1;
    end while;
END $$
DELIMITER ;
CALL  create_fileobject_delete_log_sub_table;
DROP PROCEDURE IF EXISTS create_fileobject_delete_log_sub_table;

DROP TABLE IF EXISTS `slice_fileobject`;
CREATE TABLE `slice_fileobject` (
  `objectid` varchar(64) NOT NULL,
  `uploadid` varchar(64) NOT NULL,
  `callbackkey` varchar(256) NOT NULL,
  `partsize` bigint(20) NOT NULL,
  `state` int(11) DEFAULT NULL,
  `path` varchar(2048) DEFAULT NULL,
  `mergeAt` datetime DEFAULT NULL,
  `mergeTimes` int(11) DEFAULT 0,
  `modified` datetime DEFAULT NULL,
  `etag` varchar(24) DEFAULT NULL,
  `partCRC` varchar(128) DEFAULT NULL,  
  PRIMARY KEY (`objectid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


--
-- Table structure for table `fileobject_attachment_0`
--
DROP TABLE IF EXISTS `fileobject_attachment_0`;
CREATE TABLE `fileobject_attachment_0` (
  `objectid` varchar(64) NOT NULL,
  `attachment` varchar(64) NOT NULL,
  `length` BIGINT(20) DEFAULT 0,
  `path` varchar(2048) NOT NULL,
  PRIMARY KEY (`objectid`,`attachment`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
DROP PROCEDURE IF EXISTS create_fileobject_attachment_sub_table;
DELIMITER $$
CREATE PROCEDURE create_fileobject_attachment_sub_table()
sql security invoker
BEGIN
    declare aa varchar(256);
    declare i int default 1;
    while i<100 do
        set aa=CONCAT('fileobject_attachment_',i);
        set @droptable= CONCAT(' drop table IF EXISTS ', aa);
        set @createtable= CONCAT(' create table ',aa ,' like fileobject_attachment_0');
        prepare stmt from @droptable;
        execute stmt;
        prepare stmt from @createtable;
        execute stmt;
        set i=i+1;
    end while;
END $$
DELIMITER ;
CALL  create_fileobject_attachment_sub_table;
DROP PROCEDURE IF EXISTS create_fileobject_attachment_sub_table;
