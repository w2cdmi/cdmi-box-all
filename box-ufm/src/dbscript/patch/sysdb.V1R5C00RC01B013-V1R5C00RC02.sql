use sysdb;
update system_config set value=10000000 where id='synchronous.user.max.nodenum';
update system_config set value=10000000 where id='user.max.file.num';

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

insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('systemFileScanTask','ufm','systemFileScanTask',1,'systemFileScanTask','',1,'0 0/1 0-6 * * ?','100','1',-1,-1,'1','');

insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('systemFileScanTask','ufm','0','0',1);

insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('CopyTaskDeletor','ufm','CopyTaskDeletor',1,'CopyTaskDeletor','',1,'0 0/20 * * * ?','100','1',-1,-1,'1','');
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('CopyTaskDeletor','ufm','0','0',1);

insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('userDataMigrationTaskJob','ufm','userDataMigrationTaskJob',1,'userDataMigrationTaskJob','',3,'0 0/5 * * * ?','100','1',120000,300000,'0','');
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('userDataMigrationTaskJob','ufm','0','0',1);
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('migrationTaskCheckTimer','ufm','migrationTaskCheckTimer',1,'migrationTaskCheckTimer','',1,'0 0 0/1 * * ?','100','1',-1,-1,'0','');
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('migrationTaskCheckTimer','ufm','0','0',1);
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('MigrationEverydayProcessTask','ufm','MigrationEverydayProcessTask',1,'MigrationEverydayProcessTask','',1,'0 0 23 * * ?','100','1',-1,-1,'0','');
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('MigrationEverydayProcessTask','ufm','0','0',1);

INSERT INTO system_config VALUES ('user.data.migration.task.max', '100', -1);
INSERT INTO system_config VALUES ('user.data.migration.task.retain.days', '30', -1);

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

INSERT INTO system_config VALUES ('mirror.use.slavedb', 'false', -1);

alter table db_addr add column mainAddr varchar(255) default null;
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