use sysdb;

alter table account modify `maxSpace` bigint DEFAULT 999999999999;
alter table account add currentMember Integer(11) default NULL;
alter table account add currentTeamspace Integer(11) default NULL;
alter table account add currentSpace bigint(20) default NULL;
alter table account add currentFiles bigint(20) default NULL;

alter table account_accesskey modify secretKey varchar(2048) NOT NULL;
alter table account_accesskey add `secretKeyEncodeKey` varchar(2048) NOT NULL after `secretKey`;

alter table admin modify dynamicPassword varchar(2048) DEFAULT NULL;
alter table admin add `lastLoginIP` varchar(255) DEFAULT NULL;

alter table authapp drop column secretKey;
alter table authapp add `createBy` varchar(64) DEFAULT NULL;

alter table authapp_accesskey modify secretKey varchar(2048) NOT NULL;
alter table authapp_accesskey add `secretKeyEncodeKey` varchar(2048) NOT NULL after `secretKey`;
alter table authapp_accesskey add `firstScan` int(11) DEFAULT '0';

DROP TABLE IF EXISTS `clear_recyclebin_record`;
CREATE TABLE `clear_recyclebin_record`(
	`ownedBy` bigint(20) NOT NULL,  
	`accountId` bigint(20) NOT NULL,
  `createdAt` datetime DEFAULT NULL,
	PRIMARY KEY (`OwnedBy`,`createdAt`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `files_add`;
CREATE TABLE `files_add`(
	`ownedBy` bigint(20) NOT NULL,
	`nodeId` bigint(20) NOT NULL,
	`accountId` bigint(20) NOT NULL,
	`size` bigint(20) DEFAULT NULL,  
	PRIMARY KEY (`OwnedBy`,`nodeId`),
  KEY `UserIndex` (`ownedBy`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

 
DROP TABLE IF EXISTS `files_delete`;
CREATE TABLE `files_delete`(
	`ownedBy` bigint(20) NOT NULL,
	`nodeId` bigint(20) NOT NULL,
	`accountId` bigint(20) NOT NULL,
	`size` bigint(20) DEFAULT NULL,  
	PRIMARY KEY (`OwnedBy`,`nodeId`),
  KEY `UserIndex` (`ownedBy`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

alter table license_file change `status` `status` smallint(6) DEFAULT NULL after `name`;

alter table resource_group add column type int(11) DEFAULT 0 after `dcId`;
alter table resource_group change `getProtocol` `getProtocol` varchar(128) DEFAULT 'https' after `domainName`;
alter table resource_group change `putProtocol` `putProtocol` varchar(128) DEFAULT 'https' after `getProtocol`;

alter table statistics_accesskey modify secretKey varchar(2048) NOT NULL;
alter table statistics_accesskey add `secretKeyEncodeKey` varchar(2048) NOT NULL after `secretKey`;

alter table system_config modify `value` varchar(2048) NOT NULL;

alter table system_logo change `titleEn` `titleEn` varchar(512) DEFAULT NULL after `title`;
alter table system_logo change `copyrightEn` `copyrightEn` varchar(512) DEFAULT NULL after `copyright`;

alter table teamspace change `ownerBy` `ownerBy` bigint(20) NOT NULL after `description`;

DROP PROCEDURE IF EXISTS modify_teamspace_memberships_sub_table;
DELIMITER $$
CREATE PROCEDURE modify_teamspace_memberships_sub_table()
SQL SECURITY INVOKER
BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 0;
    WHILE i<50 DO
        SET aa=CONCAT('teamspace_memberships_',i);
        SET @createobjfiles= CONCAT(' alter table ',aa ,' change `loginName` `loginName` varchar(255) NOT NULL after `username`');
        PREPARE stmt FROM @createobjfiles;
        EXECUTE stmt;
        SET i=i+1;
    END WHILE;
END $$
DELIMITER ;
CALL  modify_teamspace_memberships_sub_table;
DROP PROCEDURE IF EXISTS modify_teamspace_memberships_sub_table;

DROP PROCEDURE IF EXISTS modify_teamspace_memberships_user_sub_table;
DELIMITER $$
CREATE PROCEDURE modify_teamspace_memberships_user_sub_table()
SQL SECURITY INVOKER
BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 0;
    WHILE i<50 DO
        SET aa=CONCAT('teamspace_memberships_user_',i);
        SET @createobjfiles= CONCAT(' alter table ',aa ,' change `loginName` `loginName` varchar(255) NOT NULL after `username`');
        PREPARE stmt FROM @createobjfiles;
        EXECUTE stmt;
        SET i=i+1;
    END WHILE;
END $$
DELIMITER ;
CALL  modify_teamspace_memberships_user_sub_table;
DROP PROCEDURE IF EXISTS modify_teamspace_memberships_user_sub_table;

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

insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`)
	values ('spaceStatisticsTask','ufm','spaceStatisticsTask',1,'spaceStatisticsTask','',1,'0 0/3 * * * ?','100','1',-1,-1,'0','');

insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`)
	values ('spaceStatisticsTask','ufm','0','0',1); 

insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`)
	values ('modifySpaceDBTask','ufm','modifySpaceDBTask',1,'modifySpaceDBTask','',1,'0 0 0/1 * * ?','100','1',-1,-1,'0','');

insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`)
	values ('modifySpaceDBTask','ufm','0','0',1);

insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`)
	values ('clearRecycleBinTask','ufm','clearRecycleBinTask',1,'clearRecycleBinTask','',1,'0 10 0/1 * * ?','100','1',-1,-1,'0','');

insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('clearRecycleBinTask','ufm','0','0',1);

insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) 
	values ('isystemNetworkCheckJob','isystem','isystemNetworkCheckJob',1,'isystemNetworkCheckJob','5',0,'0/15 * * * * ?','100','1',-1,-1,'0','');
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('isystemNetworkCheckJob','isystem','0','0',1);

insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) 
	values ('ufmNetworkCheckJob','ufm','ufmNetworkCheckJob',1,'ufmNetworkCheckJob','5',0,'0/15 * * * * ?','100','1',-1,-1,'0','');
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('ufmNetworkCheckJob','ufm','0','0',1);


insert into system_config values ('mailServer.mailSecurity', 'tls', -1);

insert into system_config values ('system.linkcode.security.enable', 'true', -1);
insert into system_config values ('system.linkcode.security.byte.size', '16', -1);

INSERT INTO system_config VALUES ('lockcount', '5', -1);
INSERT INTO system_config VALUES ('lockperiod', '86400', -1);
INSERT INTO system_config VALUES ('locktime', '300', -1);

INSERT INTO system_config VALUES ('nearestStore.Status', '0', -1);

insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) 
	values ('CopyTaskMonitor','ufm','CopyTaskMonitor',1,'CopyTaskMonitor','',1,'0 0/2 * * * ?','100','1',-1,-1,'1','');
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('CopyTaskMonitor','ufm','0','0',1);

insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('CopyTaskDeletor','ufm','CopyTaskDeletor',1,'CopyTaskDeletor','',1,'0 0/20 * * * ?','100','1',-1,-1,'1','');
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('CopyTaskDeletor','ufm','0','0',1);

insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('systemFileScanTask','ufm','systemFileScanTask',1,'systemFileScanTask','',1,'0 0/1 0-6 * * ?','100','1',-1,-1,'1','');

insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('systemFileScanTask','ufm','0','0',1);


insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('userDataMigrationTaskJob','ufm','userDataMigrationTaskJob',1,'userDataMigrationTaskJob','',3,'0 0/5 * * * ?','100','1',120000,300000,'0','');
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('userDataMigrationTaskJob','ufm','0','0',1);
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('migrationTaskCheckTimer','ufm','migrationTaskCheckTimer',1,'migrationTaskCheckTimer','',1,'0 0 0/1 * * ?','100','1',-1,-1,'0','');
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('migrationTaskCheckTimer','ufm','0','0',1);
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('MigrationEverydayProcessTask','ufm','MigrationEverydayProcessTask',1,'MigrationEverydayProcessTask','',1,'0 0 23 * * ?','100','1',-1,-1,'0','');
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('MigrationEverydayProcessTask','ufm','0','0',1);

INSERT INTO system_config VALUES ('user.data.migration.task.max', '100', -1);
INSERT INTO system_config VALUES ('user.data.migration.task.retain.days', '30', -1);

INSERT INTO system_config VALUES ('mirror.use.slavedb', 'false', -1);

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

update system_job_def set pauseable=0 where name='spaceStatisticsTask' and model='ufm';
update system_job_def set pauseable=0 where name='copyPolicyStatisticTimer' and model='ufm';
update system_job_def set pauseable=0 where name='modifySpaceDBTask' and model='ufm';
update system_job_def set pauseable=0 where name='copyPolicyStatisticExecuteTask' and model='ufm';
update system_job_def set pauseable=0 where name='isystemNetworkCheckJob' and model='isystem';
update system_job_def set pauseable=0 where name='clearRecycleBinTask' and model='ufm';
update system_job_def set pauseable=0 where name='concStatisticsJob' and model='ufm';
update system_job_def set pauseable=0 where name='ufmNetworkCheckJob' and model='ufm';
update system_job_def set pauseable=0 where name='asyncSendMessageTask' and model='ufm';
update system_job_def set pauseable=0 where name='copyTaskRouter' and model='ufm';
update system_job_def set pauseable=0 where name='writeLogDbJob' and model='ufm';
update system_job_def set pauseable=0 where name='createUserLogTablesTask' and model='ufm';
update system_job_def set pauseable=0 where name='refreshResourceGroupStatusJob' and model='ufm';
update system_job_def set pauseable=0 where name='syncHistoryFileDeleteTask' and model='ufm';
update system_job_def set pauseable=0 where name='userStatisticsJob' and model='ufm';
INSERT INTO system_config VALUES ('user.migration.task.scan.timeout.second', '1200', -1);

update sysdb.account set maxSpace = 999999999999 where maxSpace = 99999999;

alter table resource_group add rwStatus int(11) default 0 after status;


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