--
-- Table structure for data migration (shenzhen to dongguan) for log
--
use ds_sysdb;
DROP TABLE IF EXISTS `copyExeTime`;
CREATE TABLE `copyExeTime` (
`copyTaskId`  varchar(36) NOT NULL ,
`size`  bigint(20) NULL ,
`exeAgent`  varchar(36) NULL ,
`startTime`  datetime NULL ,
`endTime`  datetime NULL ,
`beginDown`  datetime NULL ,
`beginWrite`  datetime NULL ,
`endWrite`  datetime NULL 
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE fs_endpoint_baseinfo add COLUMN `beststartrange` bigint(20) not null default 0;
ALTER TABLE fs_endpoint_baseinfo add COLUMN `bestendrange` bigint(20) not null default -1;
ALTER TABLE fs_endpoint_baseinfo add COLUMN `multipartfirst` int(11) not null default 0;


insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) 
    values ('copyTaskDistributeTask','dss','copyTaskDistributeTask',1,'copyTaskDistributeTask','',1,'0/5 * * * * ?', '100', '1', '-1', '-1', '0', '0');

delete from `system_job_def` where name='deleteFileObjectTask';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) 
	values ('deleteFileObjectTask','dss','deleteFileObjectTask',1,'deleteFileObjectTask','{"tableCount":10,"timeout":60,"retryTimes":10,"reserveTime":30,"maxTimeConsuming":1500000, "limit":5, "deleteWait":1000}',1,'25 4,34 0,1,2,3,4,5,6,7,20,21,22,23 * * ?','100','1',-1,-1,'1','1');

delete from `system_job_def` where name='mergerMultipartJob';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) 
	values ('mergerMultipartJob','dss','mergerMultipartJob',1,'multipartFileObjectMergeTask','{"timeout":72,"maxMergeTimes":3,"needDeleteParts":"true","minPartSizeForMerge":0,"needMerge":{"uds":"false","normal":"true","css":"true","n8500":"true","nas":"true"}}',3,'','100','5',3600000,1800000,'0','');

delete from `system_job_def` where name='multipartFileObjectClearTask';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) 
	values ('multipartFileObjectClearTask','dss','multipartFileObjectClearTask',1,'multipartFileObjectClearTask','{"timeout":72,"maxMergeTimes":3,"reserveTime":1440,"maxTimeConsuming":1500000,"limit":100, "clearWait":1000, "maxClearTimes":3}',1,'25 6,36 0,1,2,3,4,5,6,7,20,21,22,23 * * ?','100','1',-1,-1,'1','1');
	
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('copyTaskDistributeTask','dss',0,0,1);
delete from `system_job_runtimeinfo` where jobName='deleteFileObjectTask';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('deleteFileObjectTask','dss',0,0,1);
delete from `system_job_runtimeinfo` where jobName='multipartFileObjectClearTask';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('multipartFileObjectClearTask','dss',0,0,1);
--
-- Table structure for realtime remote replication
--
DROP TABLE IF EXISTS `copyTask_data_rt`;
DROP TABLE IF EXISTS `copytask_data_rt`;
CREATE TABLE `copytask_data_rt` (
  `taskId` VARCHAR(64) NOT NULL,
  `nodeSize` bigint(20) NOT NULL,
  `triggerType` int(2) NOT NULL,
  `status` int(2) default 0,
  `createTime` DATETIME NOT NULL,
  `modifyTime` DATETIME NOT NULL,
  `srcObjectId` VARCHAR(36) NOT NULL,
  `destObjectId` VARCHAR(36) NOT NULL,
  `exeAgent` varchar(50),
  `md5` VARCHAR(32),
  `blockMD5` VARCHAR(32),
  `errorCode` int(4) default null,
  `retryNum` int(2) default 0,
  PRIMARY KEY (`taskId`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `copytask_part_rt`;
CREATE TABLE `copytask_part_rt` (
  `taskId` VARCHAR(64) NOT NULL,
  `partId` VARCHAR(64) NOT NULL,
  `size` bigint(20) NOT NULL,
  `status` int(2) default 0,
  `createTime` DATETIME NOT NULL,
  `modifyTime` DATETIME NOT NULL,
  `partRange` varchar(100) default 0,
  PRIMARY KEY (`taskId`,`partId`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

INSERT INTO `ds_sysdb`.`system_job_def` (`name`,`model`, `description`, `state`, `beanname`, `parameter`, `type`, `cron`, `recordnumber`, `threadnumber`, `datawait`, `clusterjobwait`, `pauseable`, `changeables`) VALUES ('realTimeMonitor','dss', 'realTimeMonitor', '1', 'realTimeMonitor', '', '0', '* 0/5 * * * ?', '100', '1', '-1', '-1', '1', '0');
INSERT INTO `ds_sysdb`.`system_job_runtimeinfo` (`jobName`, `model`,`totalSuccess`, `totalFailed`, `lastResult`) VALUES ('realTimeMonitor', 'dss','0', '0', '0');

alter table `slice_fileobject` add column clearTimes int(11) default 0;
DROP TABLE IF EXISTS `slice_fileobject_failed`;
CREATE TABLE `slice_fileobject_failed` like slice_fileobject;

update system_config t set t.value='128' where t.key='fs.normal.subfolder.number';
insert into `system_config` values ('fs.normal.subfolder.path.generator','com.huawei.sharedrive.dataserver.filesystem.normal.PathGenerator','fs.normal.subfolder.path.generator',1,1);

INSERT INTO `system_config` (`key`, `value`, `description`, `showable`, `changeable`) VALUES ('realtime.default.split.size', '20971520', 'realtime.default.split.size', '1', '1');