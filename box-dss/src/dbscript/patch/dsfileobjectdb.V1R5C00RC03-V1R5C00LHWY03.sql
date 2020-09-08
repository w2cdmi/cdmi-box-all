use ds_fileobjectdb;

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