CREATE DATABASE  IF NOT EXISTS `ds_sysdb`;
USE `ds_sysdb`;

DROP TABLE IF EXISTS `copy_task`;
CREATE TABLE `copy_task` (
  `taskId` VARCHAR(36) NOT NULL,
  `srcObjectId` VARCHAR(36) NOT NULL,
  `destObjectId` VARCHAR(36) NOT NULL,
  `size` bigint(20) NOT NULL DEFAULT 0,
  `copyStatus` int(2) default 0,
  `remark` VARCHAR(256),
  `createdAt` DATETIME NOT NULL,
  `modifiedAt` DATETIME NOT NULL,
  `exeAgent` VARCHAR(36),
  `priority` int(2) default 0,
  `retryNum` int(2) default 0,
  `md5` VARCHAR(32),
  `blockMD5` VARCHAR(32),
  `errorCode` int(4) default null,
  PRIMARY KEY (`taskId`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `copy_task` ADD UNIQUE INDEX `copy_task_id` (`taskId`) ;
ALTER TABLE `copy_task` ADD UNIQUE INDEX `copy_task_destobjid` (`destObjectId`) ;
ALTER TABLE `copy_task` ADD INDEX `copy_task_agent_status` (`copyStatus`, `exeAgent`);


DROP TABLE IF EXISTS `copy_task_part`;
CREATE TABLE `copy_task_part` (
  `taskId` VARCHAR(36) NOT NULL,
  `partId` VARCHAR(36) NOT NULL,
  `size` bigint(20) NOT NULL DEFAULT 0,
  `copyStatus` int(2) default 0,
  `remark` VARCHAR(256),
  `createdAt` DATETIME NOT NULL,
  `modifiedAt` DATETIME NOT NULL,
  `retryNum` int(2) default 0,
  `partRange` VARCHAR(100),
  PRIMARY KEY (`taskId`,`partId`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `copy_task_part` ADD UNIQUE INDEX `copy_task_part_id` (`taskId`,`partId`);



drop table if exists db_version;
CREATE TABLE `db_version` (                            
    `version` varchar(128) NOT NULL,                     
    `modified` date NOT NULL,                            
    `description` varchar(2048) NOT NULL,                
    `sequence` int(11) NOT NULL AUTO_INCREMENT,          
    PRIMARY KEY (`version`),
    KEY `sequence` (`sequence`)                                     
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;
insert into db_version(version, modified, description) values('1.3.00.3601', '2014-11-25', '');
  
--
-- Table structure for table `default_user_config`
--
DROP TABLE IF EXISTS `db_addr`;
CREATE TABLE `db_addr` (
  `dbId` varchar(128) NOT NULL,
  `masterAddr` varchar(255) NOT NULL,
  `slaveAddr` varchar(255) NOT NULL,
  PRIMARY KEY (`dbId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `system_config`
--
drop table if exists system_config;
CREATE TABLE `system_config` (     
	 `key` varchar(128) NOT NULL,              
   `value` varchar(1024) DEFAULT NULL,              
   `description` varchar(256) DEFAULT NULL,  
   `showable` int DEFAULT 0, 
   `changeable` int DEFAULT 0, 
   PRIMARY KEY (`key`)                       
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
insert into `system_config` values ('fs.endpoint.select.algorithm','pw.cdmi.file.engine.filesystem.manage.SelectBestStorageAlgorithm','fs.endpoint.select.algorithm',0,0);
insert into `system_config` values ('fs.normal.subfolder.path.generator','pw.cdmi.file.engine.filesystem.normal.PathGenerator','fs.normal.subfolder.path.generator',1,1);
insert into `system_config` values ('fs.normal.subfolder.level','2','fs.normal.subfolder.level',1,1);
insert into `system_config` values ('fs.normal.subfolder.number','3000','fs.normal.subfolder.number',1,1);
insert into `system_config` values ('fs.normal.subfolder.prefix','folder_','fs.normal.subfolder.prefix',1,1);
insert into `system_config` values ('fs.preview.pdf2swf.commond','/opt/tomcat_dss/bin/pdf2swf','fs.preview.pdf2swf.commond',1,1);
insert into `system_config` values ('fs.preview.pdf2swf.success.message','Writing SWF file','fs.preview.pdf2swf.success.message',1,1);
insert into `system_config` values ('fs.preview.support.list','doc;docx;ppt;pptx;xls;xlsx;txt;rtf;pdf;odt;ods;odp','fs.preview.support.list',1,1);
insert into `system_config` values ('fs.preview.swf.convert.maxpage','50','fs.preview.swf.convert.maxpage',1,1);
insert into `system_config` values ('fs.preview.temp.dir','/opt/ramdisk/previewtemp/','fs.preview.temp.dir',1,1);
insert into `system_config` values ('fs.storage.check.failed.timeout','120','fs.storage.check.failed.timeout',1,1);
insert into `system_config` values ('fs.storage.check.task.timeout','30','fs.storage.check.task.timeout',1,1);
insert into `system_config` values ('fs.storage.check.timeout','50','fs.storage.check.timeout',1,1);
insert into `system_config` values ('fs.storage.multipart.maxpartid','10000','fs.storage.multipart.maxpartid',1,1);
insert into `system_config` values ('fs.uds.check.retry','3','fs.uds.check.retry',1,1);
insert into `system_config` values ('object.callback.retry.interval','2000','object.callback.retry.interval',1,1);
insert into `system_config` values ('object.callback.retry.times','3','object.callback.retry',1,1);
insert into `system_config` values ('support.filesystem.types','css;normal;n8500;uds;nas;aws_s3;qcloud_os;aliyun_oss;tencent_cloud_cos;','support.filesystem.types',1,0);
insert into `system_config` values ('support.filesystem.uds.https.only','false','support.filesystem.uds.https.only',1,0);
insert into `system_config` values ('system.cluster.node.timeout','30000','system.cluster.node.timeout',0,0);
insert into `system_config` values ('system.job.cluster.checkin.interval','120000','system.job.cluster.checkin.interval',1,0);
insert into `system_config` values ('system.job.cluster.threadcount','5','system.job.cluster.threadcount',1,0);
insert into `system_config` values ('system.job.cron.threadcount','5','system.job.cron.threadcount',1,0);
insert into `system_config` values ('system.linux.shell.path','/bin/sh','linux.shell.path',1,1);
insert into `system_config` values ('system.logfile.backup.path','','system.logfile.backup.path',1,1);
insert into `system_config` values ('system.object.delete.real','true','system.object.delete.real',0,0);
insert into `system_config` values ('system.environment.cluster.node.timeout','120000','system.environment.cluster.node.timeout',1,1);
insert into `system_config` values ('system.environment.service.get.protocol','https','system.environment.service.get.protocol',1,1);
insert into `system_config` values ('system.environment.service.put.protocol','https','system.environment.service.put.protocol',1,1);
insert into `system_config` values ('system.environment.service.port.http','8282','system.environment.service.port.http',1,1);
insert into `system_config` values ('system.environment.service.port.https','8446','system.environment.service.port.https',1,1);
insert into `system_config` values ('system.environment.service.path','','system.environment.service.path',1,1);
insert into `system_config` values ('system.environment.health.record.file','/opt/tomcat_dss/security/health.tmp','system.environment.health.record.file',1,1);
insert into `system_config` values ('system.datacenter.been.initialized','false','system.datacenter.been.initialized',1,0);
insert into `system_config` values ('system.datacenter.id','','system.datacenter.id',0,0);
insert into `system_config` values ('system.datacenter.type','merge','system.datacenter.type',1,1);
insert into `system_config` values ('thrift.appserver.request.timeout','30000','thrift.appserver.request.timeout',1,1);
insert into `system_config` values ('thrift.appserver.server.ip','','thrift.appserver.server.ip',1,1);
insert into `system_config` values ('thrift.appserver.server.port','','thrift.appserver.server.port',1,1);
insert into `system_config` values ('thrift.dataserver.publish.port','13010','thrift.dataserver.publish.port',1,1);
insert into `system_config` values ('uds.buckets.number','20','uds.buckets.number',1,1);
insert into `system_config` values ('system.inner.loadbalance.enable','true','system.inner.loadbalance.enable',1,1);
insert into `system_config` values ('system.inner.loadbalance.try.counts','3','system.inner.loadbalance.try.counts',1,1);
insert into `system_config` values ('system.sk.cache.timeout','86400000','system.sk.cache.timeout',1,1);
insert into `system_config` values ('mirror.global.enable','0','mirror.global.enable',0,1);
insert into `system_config` values ('mirror.global.task.state','0','mirror.global.task.state',0,1);
insert into `system_config` values ('mirror.global.enable.timer','1','mirror.global.enable.timer',0,1);

drop table if exists fs_endpoint_baseinfo;
CREATE TABLE `fs_endpoint_baseinfo` (               
  `id` varchar(64) NOT NULL,           
  `endpoint` varchar(4096) BINARY NOT NULL,       
  `fstype` varchar(512) NOT NULL,          
  `state` int(11) DEFAULT 0,
  `nospace` int(11) NOT NULL DEFAULT 0,
  `maxutilization` float(5,2) NOT NULL DEFAULT 0,
  `retrieval` float(5,2) NOT NULL DEFAULT 0,       
  `priority` int(11) NOT NULL DEFAULT 1,         
  `spacesize` bigint(20) NOT NULL DEFAULT '-1',     
  `usedsize` bigint(20) NOT NULL DEFAULT 0,      
  `objectnumber` bigint(20) NOT NULL DEFAULT 0, 
  `beststartrange` bigint(20) not null default 0,
  `bestendrange` bigint(20) not null default -1,
  `multipartfirst` int(11) not null default 0,
  `sequence` int(11) auto_increment ,          
  PRIMARY KEY (`id`),
  KEY (`sequence`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

drop table if exists fs_endpoint_deviceinfo;
CREATE TABLE `fs_endpoint_deviceinfo` (               
  `id` varchar(64) NOT NULL,
  `device` varchar(128) NOT NULL,              
  `writeable` int(11) NOT NULL DEFAULT 1,
  `available` int(11) NOT NULL DEFAULT 1,  
  PRIMARY KEY (`id`,`device`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

drop table if exists fs_accesspath_baseinfo;
CREATE TABLE fs_accesspath_baseinfo (
   `id` varchar(64) NOT NULL,      
   `endpointid`           varchar(64) not null,
   `state` int(11) DEFAULT 1,
   `nospace` int(11) NOT NULL DEFAULT 0,
   `type` varchar(64) DEFAULT NULL,
   `path`                 varchar(2048) BINARY NOT NULL,
   `sequence`             int(11) auto_increment,
   primary key (`id`),
   KEY (`sequence`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `ds_sysdb.fs_accesspath`
--
drop table if exists fs_accesspath_deviceinfo;
CREATE TABLE fs_accesspath_deviceinfo (
   `id` varchar(64) NOT NULL,    
   `device` varchar(128) NOT NULL,           
   `endpointid`           varchar(64) not null,
   `writeable`            int(11) NOT NULL DEFAULT 1,
   `available` 			  int(11) NOT NULL DEFAULT 1,  
   primary key (`id`,`device`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `ds_sysdb.system_clusters`
--
drop table if exists system_clusters;
CREATE TABLE system_clusters (
   `name`           varchar(128) NOT NULL,  
   `manageraddr`    	varchar(128) DEFAULT NULL,
   `managergw`    	varchar(128) DEFAULT NULL,
   `managerport`    int(11) DEFAULT NULL,         
   `inneraddr`    	varchar(128) DEFAULT NULL,
   `innergw`    	varchar(128) DEFAULT NULL,
   `serviceaddr`    varchar(128) DEFAULT NULL,
   `servicgw`    	varchar(128) DEFAULT NULL,
   `state` 			int(11) DEFAULT NULL,
   `runtimestate` 	int(11) DEFAULT 2,
   `lastreporttime` bigint(20) DEFAULT NULL,
   primary key (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
  `clearTimes` int(11) DEFAULT 0,
  `modified` datetime DEFAULT NULL,
  `etag` varchar(24) DEFAULT NULL,
  `partCRC` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`objectid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `slice_fileobject_failed`;
CREATE TABLE `slice_fileobject_failed` like slice_fileobject;

--
-- Table structure for table `ds_sysdb.system_job_def`
--
drop table if exists system_job_def;
CREATE TABLE system_job_def (
   `name`           varchar(128) NOT NULL,  
   `model` 			varchar(128) NOT NULL,         
   `description`    varchar(256) DEFAULT NULL,
   `state` int(11) NOT NULL DEFAULT 1,
   `beanname` varchar(256) NOT NULL,
   `parameter` varchar(512) DEFAULT NULL,
   `type` int(11) NOT NULL,
   `cron` varchar(64) DEFAULT NULL,
   `recordnumber`   int(11) NOT NULL default 100,
   `threadnumber`   int(11) NOT NULL default 1,
   `datawait`   bigint(20) NOT NULL default -1,
   `clusterjobwait`   bigint(20) NOT NULL default -1,
   `pauseable`   tinyint(1) NOT NULL default 1,
   `changeables` varchar(64) DEFAULT NULL,
   primary key (`name`,`model`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

drop table if exists system_job_runtimeinfo;
CREATE TABLE system_job_runtimeinfo (
   `jobName`             varchar(128) NOT NULL, 
   `model` 			varchar(128) NOT NULL,   
   `totalSuccess` bigint(20) NOT NULL DEFAULT 0,
   `totalFailed` bigint(20) NOT NULL DEFAULT 0,
   `lastResult` tinyint(1) NOT NULL DEFAULT 0,
   primary key (`jobName`,`model`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

drop table if exists system_job_executerecord;
CREATE TABLE system_job_executerecord (
   `id` bigint(20) NOT NULL,
   `jobName`             varchar(128) NOT NULL,
   `model` 			varchar(128) NOT NULL,     
   `isSuccess`           tinyint(1) NOT NULL DEFAULT 0,           
   `times`    bigint(20) DEFAULT NULL,
   `executeTime`    bigint(20) DEFAULT NULL,
   `executeMachine` varchar(256) DEFAULT NULL,
   `output` varchar(2048) DEFAULT NULL,
   primary key `idx_jobname` (`id`,`model`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('autoRecoverTask','dss','autoRecoverTask',1,'autoRecoverTask','',0,'0 0/1 * * * ?','100','1',-1,-1,'1','');
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('refreshStorageResourceJob','dss','refreshStorageResourceTask',1,'refreshStorageResourceTask','',0,'5 * * * * ?','100','1',-1,-1,'0','');
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('checkSelfStatusJob','dss','checkSelfStatusJob',1,'checkSelfStatusJob','',0,'0/4 * * * * ?','100','1',-1,-1,'0','');
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('mergerMultipartJob','dss','mergerMultipartJob',1,'multipartFileObjectMergeTask','{"timeout":72,"maxMergeTimes":3,"needDeleteParts":"true","minPartSizeForMerge":0,"needMerge":{"uds":"false","normal":"true","css":"true","n8500":"true","nas":"true"}}',3,'','100','5',3600000,1800000,'0','');
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('concurrentCheckTask','dss','concurrentCheckTask',1,'tomcatMonitor','',0,'0 * * * * ?','100','1',-1,-1,'0','');
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('clearRedundantJobExecuteRecordJob','dss','clearRedundantJobExecuteRecordJob',1,'clearRedundantJobExecuteRecordJob','',1,'15 1 0/1 * * ?','100','1',-1,-1,'1','');
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) 
	values ('reportConcStatusJob','dss','reportConcStatusJob',1,'reportConcStatusJob','',0,'0 0/5 * * * ?','100','1',-1,-1,'0','');
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) 
	values ('licenseRetryJob','dss','licenseRetryJob',1,'licenseRetryJob','',1,'0 0/10 * * * ?','100','1',-1,-1,'1','');
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) 
    values ('clearSKCacheJob','dss','clearSKCacheJob',1,'clearSKCacheJob','',0,'0 0/30 * * * ?',100,1,-1,-1,0,'');
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) 
	values ('deleteFileObjectTask','dss','deleteFileObjectTask',1,'deleteFileObjectTask','{"tableCount":10,"timeout":60,"retryTimes":10,"reserveTime":30,"maxTimeConsuming":1500000, "limit":5, "deleteWait":1000}',1,'25 4,34 0,1,2,3,4,5,6,7,20,21,22,23 * * ?','100','1',-1,-1,'1','1');
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) 
	values ('multipartFileObjectClearTask','dss','multipartFileObjectClearTask',1,'multipartFileObjectClearTask','{"timeout":72,"maxMergeTimes":3,"reserveTime":1440,"maxTimeConsuming":1500000,"limit":100, "clearWait":1000, "maxClearTimes":3}',1,'25 6,36 0,1,2,3,4,5,6,7,20,21,22,23 * * ?','100','1',-1,-1,'1','1');
	
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('autoRecoverTask','dss','0','0',1);
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('refreshStorageResourceJob','dss','0','0',1);
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('checkSelfStatusJob','dss','0','0',1);
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('mergerMultipartJob','dss','0','0',1);
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('concurrentCheckTask','dss','0','0',1);
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('clearRedundantJobExecuteRecordJob','dss','0','0',1);
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('reportConcStatusJob','dss','0','0',1);
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('licenseRetryJob','dss','0','0',1);
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('clearSKCacheJob','dss',0,0,1);
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('deleteFileObjectTask','dss',0,0,1);
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('multipartFileObjectClearTask','dss',0,0,1);


DROP TABLE IF EXISTS QRTZ_FIRED_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_PAUSED_TRIGGER_GRPS;
DROP TABLE IF EXISTS QRTZ_SCHEDULER_STATE;
DROP TABLE IF EXISTS QRTZ_LOCKS;
DROP TABLE IF EXISTS QRTZ_SIMPLE_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_SIMPROP_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_CRON_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_BLOB_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_JOB_DETAILS;
DROP TABLE IF EXISTS QRTZ_CALENDARS;

CREATE TABLE QRTZ_JOB_DETAILS(
SCHED_NAME VARCHAR(120) NOT NULL,
JOB_NAME VARCHAR(200) NOT NULL,
JOB_GROUP VARCHAR(200) NOT NULL,
DESCRIPTION VARCHAR(250) NULL,
JOB_CLASS_NAME VARCHAR(250) NOT NULL,
IS_DURABLE VARCHAR(1) NOT NULL,
IS_NONCONCURRENT VARCHAR(1) NOT NULL,
IS_UPDATE_DATA VARCHAR(1) NOT NULL,
REQUESTS_RECOVERY VARCHAR(1) NOT NULL,
JOB_DATA BLOB NULL,
PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP))
ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE QRTZ_TRIGGERS (
SCHED_NAME VARCHAR(120) NOT NULL,
TRIGGER_NAME VARCHAR(200) NOT NULL,
TRIGGER_GROUP VARCHAR(200) NOT NULL,
JOB_NAME VARCHAR(200) NOT NULL,
JOB_GROUP VARCHAR(200) NOT NULL,
DESCRIPTION VARCHAR(250) NULL,
NEXT_FIRE_TIME BIGINT(13) NULL,
PREV_FIRE_TIME BIGINT(13) NULL,
PRIORITY INTEGER NULL,
TRIGGER_STATE VARCHAR(16) NOT NULL,
TRIGGER_TYPE VARCHAR(8) NOT NULL,
START_TIME BIGINT(13) NOT NULL,
END_TIME BIGINT(13) NULL,
CALENDAR_NAME VARCHAR(200) NULL,
MISFIRE_INSTR SMALLINT(2) NULL,
JOB_DATA BLOB NULL,
PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
REFERENCES QRTZ_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP))
ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE QRTZ_SIMPLE_TRIGGERS (
SCHED_NAME VARCHAR(120) NOT NULL,
TRIGGER_NAME VARCHAR(200) NOT NULL,
TRIGGER_GROUP VARCHAR(200) NOT NULL,
REPEAT_COUNT BIGINT(7) NOT NULL,
REPEAT_INTERVAL BIGINT(12) NOT NULL,
TIMES_TRIGGERED BIGINT(10) NOT NULL,
PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))
ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE QRTZ_CRON_TRIGGERS (
SCHED_NAME VARCHAR(120) NOT NULL,
TRIGGER_NAME VARCHAR(200) NOT NULL,
TRIGGER_GROUP VARCHAR(200) NOT NULL,
CRON_EXPRESSION VARCHAR(120) NOT NULL,
TIME_ZONE_ID VARCHAR(80),
PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))
ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE QRTZ_SIMPROP_TRIGGERS
  (          
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    STR_PROP_1 VARCHAR(512) NULL,
    STR_PROP_2 VARCHAR(512) NULL,
    STR_PROP_3 VARCHAR(512) NULL,
    INT_PROP_1 INT NULL,
    INT_PROP_2 INT NULL,
    LONG_PROP_1 BIGINT NULL,
    LONG_PROP_2 BIGINT NULL,
    DEC_PROP_1 NUMERIC(13,4) NULL,
    DEC_PROP_2 NUMERIC(13,4) NULL,
    BOOL_PROP_1 VARCHAR(1) NULL,
    BOOL_PROP_2 VARCHAR(1) NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP) 
    REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))
ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE QRTZ_BLOB_TRIGGERS (
SCHED_NAME VARCHAR(120) NOT NULL,
TRIGGER_NAME VARCHAR(200) NOT NULL,
TRIGGER_GROUP VARCHAR(200) NOT NULL,
BLOB_DATA BLOB NULL,
PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
INDEX (SCHED_NAME,TRIGGER_NAME, TRIGGER_GROUP),
FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))
ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE QRTZ_CALENDARS (
SCHED_NAME VARCHAR(120) NOT NULL,
CALENDAR_NAME VARCHAR(200) NOT NULL,
CALENDAR BLOB NOT NULL,
PRIMARY KEY (SCHED_NAME,CALENDAR_NAME))
ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE QRTZ_PAUSED_TRIGGER_GRPS (
SCHED_NAME VARCHAR(120) NOT NULL,
TRIGGER_GROUP VARCHAR(200) NOT NULL,
PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP))
ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE QRTZ_FIRED_TRIGGERS (
SCHED_NAME VARCHAR(120) NOT NULL,
ENTRY_ID VARCHAR(95) NOT NULL,
TRIGGER_NAME VARCHAR(200) NOT NULL,
TRIGGER_GROUP VARCHAR(200) NOT NULL,
INSTANCE_NAME VARCHAR(200) NOT NULL,
FIRED_TIME BIGINT(13) NOT NULL,
SCHED_TIME BIGINT(13) NOT NULL,
PRIORITY INTEGER NOT NULL,
STATE VARCHAR(16) NOT NULL,
JOB_NAME VARCHAR(200) NULL,
JOB_GROUP VARCHAR(200) NULL,
IS_NONCONCURRENT VARCHAR(1) NULL,
REQUESTS_RECOVERY VARCHAR(1) NULL,
PRIMARY KEY (SCHED_NAME,ENTRY_ID))
ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE QRTZ_SCHEDULER_STATE (
SCHED_NAME VARCHAR(120) NOT NULL,
INSTANCE_NAME VARCHAR(200) NOT NULL,
LAST_CHECKIN_TIME BIGINT(13) NOT NULL,
CHECKIN_INTERVAL BIGINT(13) NOT NULL,
PRIMARY KEY (SCHED_NAME,INSTANCE_NAME))
ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE QRTZ_LOCKS (
SCHED_NAME VARCHAR(120) NOT NULL,
LOCK_NAME VARCHAR(40) NOT NULL,
PRIMARY KEY (SCHED_NAME,LOCK_NAME))
ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE INDEX IDX_QRTZ_J_REQ_RECOVERY ON QRTZ_JOB_DETAILS(SCHED_NAME,REQUESTS_RECOVERY);
CREATE INDEX IDX_QRTZ_J_GRP ON QRTZ_JOB_DETAILS(SCHED_NAME,JOB_GROUP);

CREATE INDEX IDX_QRTZ_T_J ON QRTZ_TRIGGERS(SCHED_NAME,JOB_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_JG ON QRTZ_TRIGGERS(SCHED_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_C ON QRTZ_TRIGGERS(SCHED_NAME,CALENDAR_NAME);
CREATE INDEX IDX_QRTZ_T_G ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_GROUP);
CREATE INDEX IDX_QRTZ_T_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_N_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_N_G_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_GROUP,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_NEXT_FIRE_TIME ON QRTZ_TRIGGERS(SCHED_NAME,NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_ST ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_STATE,NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_MISFIRE ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE_GRP ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_GROUP,TRIGGER_STATE);

CREATE INDEX IDX_QRTZ_FT_TRIG_INST_NAME ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,INSTANCE_NAME);
CREATE INDEX IDX_QRTZ_FT_INST_JOB_REQ_RCVRY ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,INSTANCE_NAME,REQUESTS_RECOVERY);
CREATE INDEX IDX_QRTZ_FT_J_G ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,JOB_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_FT_JG ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_FT_T_G ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP);
CREATE INDEX IDX_QRTZ_FT_TG ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,TRIGGER_GROUP);


-- create license file table--
DROP TABLE IF EXISTS `license_file`;
CREATE TABLE `license_file` (
  `id` VARCHAR(36) NOT NULL,
  `createdAt` DATETIME NOT NULL,
  `content` blob,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

-- create zookeeperInfo table--
DROP TABLE IF EXISTS `zookeeperInfo`;
CREATE TABLE `zookeeperInfo` (
  `ip` VARCHAR(128) NOT NULL,
  `port` VARCHAR(10) NOT NULL,
  PRIMARY KEY (`ip`,`port`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `system_lock`
--
DROP TABLE IF EXISTS `system_lock`;
CREATE TABLE `system_lock` (
  `id` varchar(32) NOT NULL,
  `lock` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
INSERT INTO `system_lock` VALUES ('fileobjectID',1);

DELIMITER $$
DROP PROCEDURE IF EXISTS `get_next_id` $$
CREATE PROCEDURE `get_next_id`(in param varchar(32), out returnid bigint)
sql security invoker
BEGIN
  update system_lock set `lock`=`lock`+1 where id = param;
  select `lock` into returnid from system_lock where id = param;
END $$
DELIMITER ;

INSERT INTO `ds_sysdb`.`system_job_def` (`name`,`model`, `description`, `state`, `beanname`, `parameter`, `type`, `cron`, `recordnumber`, `threadnumber`, `datawait`, `clusterjobwait`, `pauseable`, `changeables`) VALUES ('copyTaskCallbacker','dss', 'copyTaskCallbacker', '1', 'copyTaskCallbacker', '', '0', '0/5 * * * * ?', '100', '1', '-1', '-1', '0', '0');
INSERT INTO `ds_sysdb`.`system_job_def` (`name`,`model`, `description`, `state`, `beanname`, `parameter`, `type`, `cron`, `recordnumber`, `threadnumber`, `datawait`, `clusterjobwait`, `pauseable`, `changeables`) VALUES ('copyTaskStarter', 'dss','copyTaskStarter', '1', 'copyTaskStarter', '', '0', '0/5 * * * * ?', '100', '1', '-1', '-1', '0', '0');
INSERT INTO `ds_sysdb`.`system_job_def` (`name`,`model`, `description`, `state`, `beanname`, `parameter`, `type`, `cron`, `recordnumber`, `threadnumber`, `datawait`, `clusterjobwait`, `pauseable`, `changeables`) VALUES ('copyTaskRunner', 'dss','copyTaskRunner', '1', 'copyTaskRunner', '', '0', '0/5 * * * * ?', '100', '1', '-1', '-1', '0', '0');
INSERT INTO `ds_sysdb`.`system_job_runtimeinfo` (`jobName`, `model`,`totalSuccess`, `totalFailed`, `lastResult`) VALUES ('copyTaskStarter','dss', '0', '0', '0');
INSERT INTO `ds_sysdb`.`system_job_runtimeinfo` (`jobName`, `model`,`totalSuccess`, `totalFailed`, `lastResult`) VALUES ('copyTaskCallbacker', 'dss','0', '0', '0');
INSERT INTO `ds_sysdb`.`system_job_runtimeinfo` (`jobName`, `model`,`totalSuccess`, `totalFailed`, `lastResult`) VALUES ('copyTaskRunner', 'dss','0', '0', '0');

INSERT INTO `ds_sysdb`.`system_job_def` (`name`,`model`, `description`, `state`, `beanname`, `parameter`, `type`, `cron`, `recordnumber`, `threadnumber`, `datawait`, `clusterjobwait`, `pauseable`, `changeables`) VALUES ('monitorLocalCacheProducer','dss', 'monitorLocalCacheProducer', '1', 'monitorLocalCacheProducer', '', '1', '0 */5 * * * ?', '100', '1', '-1', '-1', '0', '0');
INSERT INTO `ds_sysdb`.`system_job_runtimeinfo` (`jobName`, `model`,`totalSuccess`, `totalFailed`, `lastResult`) VALUES ('monitorLocalCacheProducer', 'dss','0', '0', '0');


DROP TABLE IF EXISTS `service_node`;
CREATE TABLE `service_node` (
  `regionId` int(11) DEFAULT NULL,
  `dcId` int(11) DEFAULT NULL,
  `clusterId` int(11) NOT NULL,
  `clusterType` varchar(64) NOT NULL,
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

-- insert into `system_config`  VALUES ('mirror.global.enable.timer','1','mirror.global.enable.timer',0,1);

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


insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) 
    values ('copyTaskDistributeTask','dss','copyTaskDistributeTask',1,'copyTaskDistributeTask','',1,'0/5 * * * * ?', '100', '1', '-1', '-1', '0', '0');

insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('copyTaskDistributeTask','dss',0,0,1);

--
-- Table structure for realtime remote replication
--
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

INSERT INTO `system_config` (`key`, `value`, `description`, `showable`, `changeable`) VALUES ('realtime.default.split.size', '20971520', 'realtime.default.split.size', '1', '1');