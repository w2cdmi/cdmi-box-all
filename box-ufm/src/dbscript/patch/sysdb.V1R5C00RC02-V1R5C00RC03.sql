use cse_sysdb;

update cse_sysdb.account set maxSpace = 999999999999 where maxSpace = 99999999;

insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('MigrationEverydayProcessTask','ufm','MigrationEverydayProcessTask',1,'MigrationEverydayProcessTask','',1,'0 0 23 * * ?','100','1',-1,-1,'0','');
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('MigrationEverydayProcessTask','ufm','0','0',1);

INSERT INTO system_config VALUES ('mirror.use.slavedb', 'false', -1);


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


DROP TABLE IF EXISTS `time_config`;
CREATE TABLE `time_config` (
  `uuid` varchar(36) NOT NULL ,
  `createdAt` datetime DEFAULT NULL,
  `exeStartAt` varchar(32) DEFAULT NULL,
  `exeEndAt` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;

INSERT INTO system_config VALUES ('timeconfig.enable', 'false', -1);

insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) 
values ('checkCopyRunTimer','isystem','checkCopyRunTimer',1,'checkCopyRunTimer','',1,'0 0/2 * * * ?','100','1',-1,-1,'1','');

insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('checkCopyRunTimer','isystem','0','0',1);

INSERT INTO system_config VALUES ('mirror.global.enable.timer', 'true', -1);

ALTER TABLE data_center ADD priority int(3) DEFAULT 0;

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


delete from system_job_def where NAME='checkCopyRunTimer' and model='isystem';
delete from system_job_runtimeinfo where jobName='checkCopyRunTimer' and model ='isystem';

update system_job_def set state = 0 where name='migrationTaskCheckTimer';
update system_job_def set cron='0 0/3 * * * ?' where name = 'copyTaskRouter';