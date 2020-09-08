CREATE DATABASE  IF NOT EXISTS `sysdb`;
USE `sysdb`;

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
  `mainAddr` varchar(255) default NULL,
  PRIMARY KEY (`dbId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



--
-- Table structure for table `default_user_config`
--
DROP TABLE IF EXISTS `default_user_config`;
CREATE TABLE `default_user_config` (
  `id` varchar(128) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `dataType` tinyint(4) NOT NULL,
  `dataScope` varchar(255) DEFAULT NULL,
  `value` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `default_user_permission`
--
DROP TABLE IF EXISTS `default_user_permission`;
CREATE TABLE `default_user_permission` (
  `id` varchar(128) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `dataType` tinyint(4) NOT NULL,
  `dataScope` varchar(255) DEFAULT NULL,
  `dependence` text,
  `value` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for `authapp`
-- ----------------------------
DROP TABLE IF EXISTS `authapp`;
CREATE TABLE `authapp` (
  `authAppId` varchar(64) NOT NULL,
  `name` varchar(64) DEFAULT NULL,
  `status` tinyint(2) DEFAULT '0',
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  `uamAdminEmail` varchar(64) DEFAULT NULL,
  `authUrl` varchar(64) DEFAULT NULL,
  `description` varchar(512) DEFAULT NULL,
  `nearestStore` tinyint(4) DEFAULT 0,
  `qosPort` int(11) DEFAULT NULL,
  `createBy` varchar(64) DEFAULT NULL,
  UNIQUE KEY `authAppId_UNIQUE` (`authAppId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
INSERT INTO authapp(`authAppId`, `name`, `status`) VALUES ('PreviewPlugin', 'PreviewPlugin', '0');
INSERT INTO authapp(`authAppId`, `name`, `status`) VALUES ('SecurityScan', 'SecurityScan', '0');


-- ----------------------------
-- Table structure for `authapp_accesskey`
-- ----------------------------
DROP TABLE IF EXISTS `authapp_accesskey`;
CREATE TABLE `authapp_accesskey` (
  `id` varchar(128) NOT NULL,
  `secretKey` varchar(2048) NOT NULL,
  `secretKeyEncodeKey` varchar(2048) NOT NULL,
  `appId` varchar(64) NOT NULL,
  `createdAt` datetime DEFAULT NULL,
  `firstScan` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `auth_code`
--
DROP TABLE IF EXISTS `auth_code`;
CREATE TABLE `auth_code` (
   code VARCHAR(32) NOT NULL,
   auth VARCHAR(255),
   user_id BIGINT NOT NULL,
   client_id VARCHAR(32) NOT NULL,
   device_id VARCHAR(128),
   created_at DATETIME NOT NULL,
   UNIQUE KEY `code` (`code`),
   UNIQUE KEY `user` (`client_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `auth_client`
--
DROP TABLE IF EXISTS `auth_client`;
CREATE TABLE `auth_client` (
   id VARCHAR(32) NOT NULL,
   password VARCHAR(32),
   name VARCHAR(128),
   description VARCHAR(255),
   created_at DATETIME NOT NULL,
   modified_at DATETIME NOT NULL,
   redirect_url VARCHAR(255),
   status VARCHAR(20),
   UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `auth_token`
--
DROP TABLE IF EXISTS `auth_token`;
CREATE TABLE `auth_token` (
   token VARCHAR(32) NOT NULL,
   auth VARCHAR(255),
   code VARCHAR(32) NOT NULL,
   type VARCHAR(20) NOT NULL,
   expired_at DATETIME,
   created_at DATETIME NOT NULL,
   refresh_token VARCHAR(32),
   user_id BIGINT NOT NULL,
   device_sn VARCHAR(128),
   UNIQUE KEY `token` (`token`),
   UNIQUE KEY `refresh_token` (`refresh_token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `system_config`
--
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config` (
  `id` varchar(128) NOT NULL,
  `value` varchar(2048) NOT NULL,
  `appId` varchar(64) DEFAULT -1,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


--
-- Table structure for table `system_lock`
--
DROP TABLE IF EXISTS `system_lock`;
CREATE TABLE `system_lock` (
  `id` varchar(32) NOT NULL,
  `lock` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
INSERT INTO `system_lock` VALUES ('adminId',1);
INSERT INTO `system_lock` VALUES ('adminLogId',1);
INSERT INTO `system_lock` VALUES ('userId',1);
INSERT INTO `system_lock` VALUES ('resourceGroupId',1);
INSERT INTO `system_lock` VALUES ('regionId',1);
INSERT INTO `system_lock` VALUES ('dcId',1);
INSERT INTO `system_lock` VALUES ('netSegment',1);
INSERT INTO `system_lock` VALUES ('accessNetWorkId',1);
INSERT INTO `system_lock` VALUES ('clientManageId',1);
INSERT INTO `system_lock` VALUES ('dnsServerId',2);

INSERT INTO `system_lock`(`id`,`lock`) VALUES ( 'copyPolicyId','1');
INSERT INTO `system_lock`(`id`,`lock`) VALUES ( 'copyPolicySiteInfoId','1');

DROP TABLE IF EXISTS `copy_task`;

CREATE TABLE `copy_task` (
  `taskId` varchar(32) NOT NULL,
  `srcOwnedBy` bigint(20) DEFAULT NULL,
  `srcINodeId` bigint(20) DEFAULT NULL,
  `srcObjectId` varchar(32) DEFAULT NULL,
   `fileName` varchar(255) DEFAULT NULL,
  `destOwnedBy` bigint(20) DEFAULT NULL,
  `destINodeId` bigint(20) DEFAULT NULL,
  `destObjectId` varchar(32) DEFAULT NULL,
  `size` bigint(20) DEFAULT NULL,
  `copyType` int(11) DEFAULT NULL COMMENT '1:Disaster Recovery，10：nearest access',
  `exeType` int(11) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  `isPop` boolean DEFAULT  '0',
  `exeStartAt` datetime DEFAULT NULL,
  `exeEndAt` datetime DEFAULT NULL,
  `policyId` int(11) DEFAULT NULL,
  `srcRegionId` int(11) DEFAULT '-1',
  `srcResourceGroupId` int(11) DEFAULT '-1',
  `destRegionId` int(11) DEFAULT '-1',
  `destResourceGroupId` int(11) DEFAULT '-1',
  `state` int(11) DEFAULT '0',
  `priority` int(11) DEFAULT '0' COMMENT '0：normal，1：Advanced',
  `exeResult` int(11) DEFAULT '0',
  PRIMARY KEY (`taskId`),
  KEY `EXE_TIME` (`exeType`,`exeStartAt`),
  KEY `STATE_PRI` (`state`,`priority`)
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

DROP TABLE IF EXISTS `region_network_distance`;
CREATE TABLE `region_network_distance` (
  `name` varchar(128) NOT NULL,
  `srcRegionId` int(11) NOT NULL ,
  `srcResourceGroupId` int(11) NOT NULL,
  `destRegionId` int(11) NOT NULL,
  `destResourceGroupId` int(11) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `uas_node`
--
DROP TABLE IF EXISTS `uas_node`;
CREATE TABLE `uas_node` (
  `managerip` varchar(45) NOT NULL,
  `managerport` varchar(45) NOT NULL,
  `inneraddr` varchar(128) NOT NULL,
  `serviceaddr` varchar(128) NOT NULL,
  `nataddr` varchar(128) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `runtimestate` int(11) DEFAULT '2',
  `lastreporttime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`managerip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `intranet`
--
DROP TABLE IF EXISTS `intranet`;
CREATE TABLE `intranet` (
  `netAddress` varchar(45) NOT NULL,
  `netMask` varchar(45) NOT NULL,
  PRIMARY KEY (`netAddress`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `dns_server`
--
DROP TABLE IF EXISTS `dns_server`;
CREATE TABLE `dns_server` (
  `id` int(11) NOT NULL,
  `manageIp` varchar(45) DEFAULT NULL,
  `managePort` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `manage_addr` (`manageIp`,`managePort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `dns_domain`
--
DROP TABLE IF EXISTS `dns_domain`;
CREATE TABLE `dns_domain` (
  `domainName` varchar(127) NOT NULL DEFAULT '',
  `dnsServer_id` int(11) NOT NULL,
  PRIMARY KEY (`domainName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `dss_domain`
--
DROP TABLE IF EXISTS `dss_domain`;
CREATE TABLE `dss_domain` (
  `domainName` varchar(127) NOT NULL DEFAULT '',
  `dss_id` int(11) NOT NULL,
  PRIMARY KEY (`domainName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `plugin_service_cluster`
--
DROP TABLE IF EXISTS `plugin_service_cluster`;
CREATE TABLE `plugin_service_cluster` (
  `clusterId` int(11) NOT NULL AUTO_INCREMENT,
  `dssId` int(11) NOT NULL,
  `appId` varchar(32) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` varchar(512) DEFAULT NULL,
  `monitorPeriod` int(11) DEFAULT 300 COMMENT 'Monitoring period, in seconds',
  `status` tinyint(4) NOT NULL COMMENT 'Status: 0 - normal; 1- Part abnormal; 2 - all abnormal',
  `lastMonitorTime` datetime DEFAULT NULL,
  PRIMARY KEY (`clusterId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `plugin_service_instance`
--
DROP TABLE IF EXISTS `plugin_service_instance`;
CREATE TABLE `plugin_service_instance` (
  `ip` varchar(32) NOT NULL,
  `clusterId` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `status` tinyint(4) NOT NULL,
  `lastMonitorTime` datetime DEFAULT NULL,
  PRIMARY KEY (`ip`,`clusterId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `plugin_router`
--
DROP TABLE IF EXISTS `plugin_service_router`;
CREATE TABLE `plugin_service_router` (
  `dssId` int(11) NOT NULL,
  `clusterId` int(11) NOT NULL,
  PRIMARY KEY (`dssId`,`clusterId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `data_center`
--
DROP TABLE IF EXISTS `data_center`;
CREATE TABLE `data_center` (
  `id` 			int(11) NOT NULL,
  `name` 		varchar(128) NOT NULL,
  `regionid` 	int(11) NOT NULL,
  `state` 		int(11) DEFAULT 0,
  `priority` 	int(3) DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `dc_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `data_center`
--
DROP TABLE IF EXISTS `resource_group`;
CREATE TABLE `resource_group` (
  `id` 				int(11) NOT NULL,
  `dcId` 			int(11) NOT NULL,
  `type` 			int(11) DEFAULT 0,
  `regionId` 		int(11) NOT NULL,
  `manageIp` 		varchar(45) DEFAULT NULL,
  `managePort` 	    int(11) NOT NULL,
  `domainName`      varchar(128) DEFAULT NULL,
  `getProtocol`      varchar(128) DEFAULT 'https',
  `putProtocol`      varchar(128) DEFAULT 'https',
  `serviceHttpPort` 	int(11) DEFAULT 8080,
  `serviceHttpsPort` 	int(11) DEFAULT 8443,
  `servicePath` 		varchar(128) DEFAULT NULL,
  `status` 			int(11) DEFAULT 0,
  `rwStatus` 			int(11) DEFAULT 0,
  `runtimeStatus` 	int(11) DEFAULT 0,
  `lastReportTime` 	bigint(20) DEFAULT NULL,
  `accessKey` 		varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `manage_addr` (`manageIp`,`managePort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `resource_group_node`
--
drop table if exists resource_group_node;
CREATE TABLE resource_group_node (
   `name`             	varchar(128) NOT NULL,
   `resourcegroupid`  	int(11) NOT NULL,
   `dcid` 				int(11) NOT NULL,
   `regionid` 			int(11) NOT NULL,   
   `managerip` 			varchar(45) DEFAULT NULL,
   `managerport` 		varchar(45) DEFAULT NULL,
   `inneraddr`    		varchar(128) DEFAULT NULL,
   `serviceaddr`    	varchar(128) DEFAULT NULL,
   `nataddr`    		varchar(128) DEFAULT NULL,
   `natpath` 			varchar(128) DEFAULT NULL,
   `state` 				int(11) DEFAULT NULL,
   `runtimestate` 		int(11) DEFAULT 2,
   `lastreporttime` 	bigint(20) DEFAULT NULL,
   `priority` 			int(11) DEFAULT NULL,
   primary key (`name`, `resourcegroupid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `region`
--
DROP TABLE IF EXISTS `region`;
CREATE TABLE `region` (
  `id` int(11) NOT NULL,
  `code` varchar(50) NOT NULL,
  `name` varchar(45) NOT NULL,
  `isdefault` tinyint(1) NOT NULL,
  `description` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `net_segment`;
CREATE TABLE `net_segment` (
  `id` bigint(20) NOT NULL,
  `startip` varchar(32) NOT NULL,
  `endip` varchar(32) NOT NULL,
  `regionid` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `access_network`
--
DROP TABLE IF EXISTS `access_network`;
CREATE TABLE `access_network` (
  `id` int(10) NOT NULL,
  `accessIp` varchar(16) NOT NULL,
  `netWorkType` int(2) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `accessIp_UNIQUE` (`accessIp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `admin`
--
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `id` bigint(20) NOT NULL,
  `type` tinyint(4) NOT NULL,
  `domainType` tinyint(4) NOT NULL,
  `roles` varchar(255) NOT NULL,
  `objectSid` varchar(255) DEFAULT NULL,
  `loginName` varchar(64) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `name` varchar(64) NOT NULL,
  `email` varchar(64) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  `lastLoginTime` datetime DEFAULT NULL,
  `validateKey` varchar(128) DEFAULT NULL,
  `dynamicPassword` varchar(2048) DEFAULT NULL,
  `status` tinyint(1) DEFAULT 1,
  `iterations` int(10) NOT NULL,
  `salt` varchar(255) NOT NULL,
  `resetPasswordAt` datetime DEFAULT NULL,
  `lastLoginIP` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `loginName_UNIQUE` (`loginName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
INSERT INTO `admin` VALUES (0,'-1','1','ADMIN_MANAGER','','admin','bc9b542a5f280c1b5506d12a4494c371a0075fbecba88f1938cddc6b13765ed408c0b5ccabfff4ca550fce7ad06a24982bbd4906adc3575f9792e6f2b1c130e1','Administrator','',now(),now(),null,null,null,'1',1000,'5b4240346266653662',null,null);

--
-- Table structure for table `client_manage`
--
DROP TABLE IF EXISTS `client_manage`;
CREATE TABLE `client_manage` (
  `id` int(10) NOT NULL,
  `type` varchar(64) NOT NULL,
  `fileName` varchar(255) NOT NULL,
  `version` varchar(64) NOT NULL,
  `size` bigint(20) NOT NULL,
  `supportSys` varchar(255) NOT NULL,
  `content` longblob NOT NULL,
  `twoDimCodeImage` blob,
  `releaseDate` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `user_terminal`
--
DROP TABLE IF EXISTS `user_terminal`;
CREATE TABLE `user_terminal` (
  `sn` varchar(128) default NULL,
  `userId` bigint(20) NOT NULL,
  `name` varchar(128) default NULL,
  `createdAt` datetime default NULL,
  `lastAccessAt` datetime default NULL,
  `lastAccessIP` varchar(45) default NULL,
  `status` tinyint(4) default NULL,
  `clientOS` varchar(255) default NULL,
  `type` tinyint(4) default NULL,
  `agent` varchar(128) default NULL,
  `token` varchar(256) default NULL,
  KEY `idx_userId` (`userId`),
  KEY `idx_sn` (`sn`),
  KEY `idx_sn_userId` USING BTREE (`sn`,`userId`),
  KEY `idx_userId_type_addr_agent` (`type`,`agent`,`lastAccessIP`,`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
--
-- Table structure for table `user`
--
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL,
  `objectSid` varchar(255) DEFAULT NULL,
  `loginName` varchar(64) NOT NULL,
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
  PRIMARY KEY (`id`),
  UNIQUE KEY `loginName_appId_UNIQUE` (`loginName`,`appId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `admin_log`
--
DROP TABLE IF EXISTS `admin_log`;
CREATE TABLE `admin_log` (                   
  `id` bigint(20) NOT NULL,   
  `logid` varchar(128) DEFAULT NULL,         
  `adminId` bigint(20) DEFAULT NULL,  
  `adminLoginName` varchar(64) DEFAULT NULL,       
  `adminShowName` varchar(64) DEFAULT NULL,      
  `operateType` int(11) DEFAULT NULL,   
  `createAt` datetime NOT NULL,              
  `success` tinyint(1) NOT NULL,     
  `description` varchar(1024) DEFAULT NULL,            
  `clientAddress` varchar(32) DEFAULT NULL,  
  `serverName` varchar(128) DEFAULT NULL,    
  `beforeOper` varchar(1024) DEFAULT NULL,   
  `afterOper` varchar(1024) DEFAULT NULL,    
  PRIMARY KEY (`id`),                        
  KEY `adminId` (`adminId`),                 
  KEY `adminLoginName` (`adminLoginName`),         
  KEY `adminShowName` (`adminShowName`)              
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `system_logo`;
CREATE TABLE `system_logo` (
  `id` int(10) NOT NULL DEFAULT '0',
  `domainName` varchar(512) DEFAULT NULL,
  `title` varchar(512) DEFAULT NULL,
  `titleEn` varchar(512) DEFAULT NULL,
  `logo` blob,
  `icon` blob,
  `copyright` varchar(512) DEFAULT NULL,
  `copyrightEn` varchar(512) DEFAULT NULL,
  `logoFormatName` varchar(32) DEFAULT NULL,
  `iconFormatName` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
INSERT INTO system_logo VALUES ('0',null,null,null,null,null,null,null,null,null);
           
--
-- Table structure for table `user_ext_attr_config`
--
DROP TABLE IF EXISTS `user_ext_attr_config`;
CREATE TABLE `user_ext_attr_config` (
  `id` varchar(128) NOT NULL,
  `name` varchar(128) NOT NULL,
  `description` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UQ_user_ext_attr_config_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DELIMITER $$
DROP PROCEDURE IF EXISTS `get_next_id` $$
CREATE PROCEDURE `get_next_id`(in param varchar(32), out returnid bigint)
sql security invoker
BEGIN
  update system_lock set `lock`=`lock`+1 where id = param;
  select `lock` into returnid from system_lock where id = param;
END $$
DELIMITER ;

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


delete from system_job_def where NAME='refreshResourceGroupStatusJob' and model='ufm';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('refreshResourceGroupStatusJob','ufm','refreshResourceGroupStatusJob',1,'refreshResourceGroupStatusJob','',1,'0 0/1 * * * ?','100','1',-1,-1,'0','');
delete from system_job_def where NAME='DeleteObjectTimingJob' and model='ufm';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('DeleteObjectTimingJob','ufm','DeleteObjectTimingJob',1,'deleteObjectTimingJob','',1,'0 0 0/1 * * ?','100','1',-1,-1,'1','');
delete from system_job_def where NAME='autoRecoverTask' and model='ufm';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('autoRecoverTask','ufm','autoRecoverTask',1,'autoRecoverTask','',0,'0 0/1 * * * ?','100','1',-1,-1,'1','');
delete from system_job_def where NAME='createUserLogTablesTask' and model='ufm';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('createUserLogTablesTask','ufm','createUserLogTablesTask',1,'createUserLogTablesTask','20',1,'0 0 1 * * ?','100','1',-1,-1,'0','');
delete from system_job_def where NAME='writeLogDbJob' and model='ufm';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('writeLogDbJob','ufm','writeLogDbJob',1,'writeLogDbJob','',0,'0 */2 * * * ?','100','1',-1,-1,'0','');
delete from system_job_def where NAME='DistributeFileScanTask' and model='ufm';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('DistributeFileScanTask','ufm','DistributeFileScanTask',1,'DistributeFileScanTask','',1,'0 0 0 * * ?','100','1',-1,-1,'1','');
delete from system_job_def where NAME='DistributeObjectScanTask' and model='ufm';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('DistributeObjectScanTask','ufm','DistributeObjectScanTask',1,'DistributeObjectScanTask','',1,'0 0 2 * * ?','100','1',-1,-1,'1','');
delete from system_job_def where NAME='clearUserTask' and model='ufm';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('clearUserTask','ufm','clearUserTask',1,'clearUserTask','',1,'0 0 3 * * ?','100','1',-1,-1,'1','');
delete from system_job_def where NAME='concurrentCheckTask' and model='ufm';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('concurrentCheckTask','ufm','concurrentCheckTask',1,'tomcatMonitor','',0,'0 * * * * ?','100','1',-1,-1,'1','');
delete from system_job_def where NAME='licenseExpirationTimeCheckTask' and model='ufm';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('licenseExpirationTimeCheckTask','ufm','licenseExpirationTimeCheckTask',1,'licenseAlarmHelper','',1,'0 30 2 * * ?','100','1',-1,-1,'1','');
delete from system_job_def where NAME='clearRedundantJobExecuteRecordJob' and model='ufm';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('clearRedundantJobExecuteRecordJob','ufm','clearRedundantJobExecuteRecordJobForUFM',1,'clearRedundantJobExecuteRecordJob','',1,'15 1 0/1 * * ?','100','1',-1,-1,'1','');
delete from system_job_def where NAME='expiredMessagesScanTask' and model='ufm';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('expiredMessagesScanTask','ufm','expiredMessagesScanTask',1,'expiredMsgScanTask','',1,'0 0 3 * * ?','100','1',-1,-1,'1','');
delete from system_job_def where NAME='nodeStatisticsJob' and model='ufm';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('nodeStatisticsJob','ufm','nodeStatisticsJob',1,'nodeStatisticsJob','',1,'0 30 0 * * ?','100','1',-1,-1,'1','');
delete from system_job_def where NAME='concStatisticsJob' and model='ufm';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('concStatisticsJob','ufm','concStatisticsJob',1,'concStatisticsJob','',1,'0 2 0/2 * * ?','100','1',-1,-1,'0','');
delete from system_job_def where NAME='userStatisticsJob' and model='ufm';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('userStatisticsJob','ufm','userStatisticsJob',1,'userStatisticsJob','',1,'0 30 1 * * ?','100','1',-1,-1,'0','');
delete from system_job_def where NAME='asyncSendMessageTask' and model='ufm';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('asyncSendMessageTask','ufm','asyncSendMessageTask',1,'asyncSendMessageTask','',2,'','100','1',300000,-1,'0','');
delete from system_job_def where NAME='syncHistoryFileDeleteTask' and model='ufm';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('syncHistoryFileDeleteTask','ufm','syncHistoryFileDeleteTask',1,'syncHistoryFileDeleteTask','1',0,'0 0 4 * * ?','100','1',-1,-1,'0','');
delete from system_job_def where NAME='expiredScanTaskCleanTask' and model='ufm';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('expiredScanTaskCleanTask','ufm','expiredScanTaskCleanTask',1,'expiredScanTaskCleanTask','3',1,'0 0 6 ? * SUN','100','1',-1,-1,'1','');
delete from system_job_def where NAME='spaceStatisticsTask' and model='ufm';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('spaceStatisticsTask','ufm','spaceStatisticsTask',1,'spaceStatisticsTask','',1,'0 0/3 * * * ?','100','1',-1,-1,'0','');
delete from system_job_def where NAME='modifySpaceDBTask' and model='ufm';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('modifySpaceDBTask','ufm','modifySpaceDBTask',1,'modifySpaceDBTask','',1,'0 0 0/1 * * ?','100','1',-1,-1,'0','');
delete from system_job_def where NAME='clearRecycleBinTask' and model='ufm';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('clearRecycleBinTask','ufm','clearRecycleBinTask',1,'clearRecycleBinTask','',1,'0 10 0/1 * * ?','100','1',-1,-1,'0','');
delete from system_job_def where NAME='ufmNetworkCheckJob' and model='ufm';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('ufmNetworkCheckJob','ufm','ufmNetworkCheckJob',1,'ufmNetworkCheckJob','5',0,'0/15 * * * * ?','100','1',-1,-1,'0','');
delete from system_job_def where NAME='mailServerCheck' and model='isystem';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('mailServerCheck','isystem','mailServerCheck',1,'mailServerServiceImpl','',0,'0 0/10 * * * ?','100','1',-1,-1,'1','');
delete from system_job_def where NAME='clearRedundantJobExecuteRecordJob' and model='isystem';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('clearRedundantJobExecuteRecordJob','isystem','clearRedundantJobExecuteRecordJobForIsystem',1,'clearRedundantJobExecuteRecordJob','',1,'15 1 0/1 * * ?','100','1',-1,-1,'1','');
delete from system_job_def where NAME='isystemNetworkCheckJob' and model='isystem';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('isystemNetworkCheckJob','isystem','isystemNetworkCheckJob',1,'isystemNetworkCheckJob','5',0,'0/15 * * * * ?','100','1',-1,-1,'0','');
delete from system_job_def where NAME='systemFileScanTask' and model='isystem';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('systemFileScanTask','ufm','systemFileScanTask',1,'systemFileScanTask','',1,'0 0/1 0-6 * * ?','100','1',-1,-1,'1','');
delete from system_job_def where NAME='copyTaskTimer' and model='ufm';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('copyTaskTimer','ufm','copyTaskTimer',1,'copyTaskTimer','',1,'0 0/5 * * * ?','100','1',-1,-1,'1','');
delete from system_job_def where NAME='copyTaskRouter' and model='ufm';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('copyTaskRouter','ufm','copyTaskRouter',1,'copyTaskRouter','',3,'0 0/3 * * * ?','100','1',120000,300000,'0','');
delete from system_job_def where NAME='distributeMirrorBackScanTask' and model='ufm';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('distributeMirrorBackScanTask','ufm','distributeMirrorBackScanTask',1,'distributeMirrorBackScanTask','0-6',1,'0 0/10 * * * ?','100','1',-1,-1,'1','');
delete from system_job_def where NAME='mirrorBackScanTask' and model='ufm';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('mirrorBackScanTask','ufm','mirrorBackScanTask',1,'mirrorBackScanTask','',3,'0 0/5 * * * ?','100','1',300000,600000,'1','');
delete from system_job_def where NAME='copyPolicyStatisticExecuteTask' and model='ufm';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('copyPolicyStatisticExecuteTask','ufm','copyPolicyStatisticExecuteTask',1,'copyPolicyStatisticExecuteTask','0-6',3,'0 0/5 * * * ?','100','1',120000,300000,'0','');
delete from system_job_def where NAME='copyPolicyStatisticTimer' and model='ufm';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('copyPolicyStatisticTimer','ufm','copyPolicyStatisticTimer',1,'copyPolicyStatisticTimer','',1,'0 0 * * * ?','100','1',-1,-1,'0','');
delete from system_job_def where NAME='CopyTaskMonitor' and model='ufm';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('CopyTaskMonitor','ufm','CopyTaskMonitor',1,'CopyTaskMonitor','',1,'0 0/2 * * * ?','100','1',-1,-1,'1','');
delete from system_job_def where NAME='CopyTaskDeletor' and model='ufm';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('CopyTaskDeletor','ufm','CopyTaskDeletor',1,'CopyTaskDeletor','',1,'0 0/20 * * * ?','100','1',-1,-1,'1','');
delete from system_job_def where NAME='userDataMigrationTaskJob' and model='ufm';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('userDataMigrationTaskJob','ufm','userDataMigrationTaskJob',1,'userDataMigrationTaskJob','',3,'0 0/5 * * * ?','100','1',120000,300000,'0','');
delete from system_job_def where NAME='migrationTaskCheckTimer' and model='ufm';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('migrationTaskCheckTimer','ufm','migrationTaskCheckTimer',0,'migrationTaskCheckTimer','',1,'0 0 0/1 * * ?','100','1',-1,-1,'0','');
delete from system_job_def where NAME='MigrationEverydayProcessTask' and model='ufm';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('MigrationEverydayProcessTask','ufm','MigrationEverydayProcessTask',1,'MigrationEverydayProcessTask','',1,'0 0 23 * * ?','100','1',-1,-1,'0','');
delete from system_job_def where NAME='monitorLocalCacheProducer' and model='ufm';
INSERT INTO `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) VALUES ('monitorLocalCacheProducer','ufm', 'monitorLocalCacheProducer', '1', 'monitorLocalCacheProducer', '', '1', '0 */5 * * * ?', '100', '1', '-1', '-1', '0', '0');
delete from system_job_def where NAME='monitorLocalCacheProducer' and model='isystem';
INSERT INTO `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) VALUES ('monitorLocalCacheProducer','isystem', 'monitorLocalCacheProducer', '1', 'monitorLocalCacheProducer', '', '1', '0 */5 * * * ?', '100', '1', '-1', '-1', '0', '0');
delete from system_job_def where NAME='checkCopyRunTimer' and model='isystem';
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('checkCopyRunTimer','isystem','checkCopyRunTimer',1,'checkCopyRunTimer','',1,'0 0/2 * * * ?','100','1',-1,-1,'1','');
delete from system_job_def where NAME='clearFilelabelJob' and model='ufm';
INSERT INTO `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) VALUES('clearFilelabelJob', 'ufm', 'clearFilelabelJob', 1, 'clearFilelabelJob', '', 0, '0 0 0 * * ?', '100','1' ,- 1 ,- 1, '0', '');

delete from system_job_runtimeinfo where jobName='systemFileScanTask' and model='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('systemFileScanTask','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='clearRecycleBinTask' and model='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('clearRecycleBinTask','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='modifySpaceDBTask' and model='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('modifySpaceDBTask','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='spaceStatisticsTask' and model='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('spaceStatisticsTask','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='refreshResourceGroupStatusJob' and model='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('refreshResourceGroupStatusJob','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='DeleteObjectTimingJob' and model='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('DeleteObjectTimingJob','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='autoRecoverTask' and model='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('autoRecoverTask','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='createUserLogTablesTask' and model='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('createUserLogTablesTask','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='writeLogDbJob' and model='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('writeLogDbJob','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='DistributeFileScanTask' and model='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('DistributeFileScanTask','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='DistributeObjectScanTask' and model='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('DistributeObjectScanTask','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='clearUserTask' and model='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('clearUserTask','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='concurrentCheckTask' and model='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('concurrentCheckTask','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='licenseExpirationTimeCheckTask' and model='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('licenseExpirationTimeCheckTask','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='clearRedundantJobExecuteRecordJob' and model='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('clearRedundantJobExecuteRecordJob','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='expiredMessagesScanTask' and model='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('expiredMessagesScanTask','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='nodeStatisticsJob' and model='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('nodeStatisticsJob','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='concStatisticsJob' and model='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('concStatisticsJob','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='userStatisticsJob' and model='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('userStatisticsJob','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='asyncSendMessageTask' and model='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('asyncSendMessageTask','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='syncHistoryFileDeleteTask' and model='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('syncHistoryFileDeleteTask','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='expiredScanTaskCleanTask' and model='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('expiredScanTaskCleanTask','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='ufmNetworkCheckJob' and model='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('ufmNetworkCheckJob','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='mailServerCheck' and model='isystem';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('mailServerCheck','isystem','0','0',1);
delete from system_job_runtimeinfo where jobName='clearRedundantJobExecuteRecordJob' and model='isystem';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('clearRedundantJobExecuteRecordJob','isystem','0','0',1);
delete from system_job_runtimeinfo where jobName='isystemNetworkCheckJob' and model='isystem';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('isystemNetworkCheckJob','isystem','0','0',1);

delete from system_job_runtimeinfo where jobName='copyTaskTimer' and model ='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('copyTaskTimer','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='mirrorBackScanTask' and model ='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('mirrorBackScanTask','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='distributeMirrorBackScanTask' and model ='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('distributeMirrorBackScanTask','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='copyTaskRouter' and model ='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('copyTaskRouter','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='copyPolicyStatisticExecuteTask' and model ='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('copyPolicyStatisticExecuteTask','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='copyPolicyStatisticTimer' and model ='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('copyPolicyStatisticTimer','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='CopyTaskMonitor' and model ='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('CopyTaskMonitor','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='CopyTaskDeletor' and model ='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('CopyTaskDeletor','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='userDataMigrationTaskJob' and model ='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('userDataMigrationTaskJob','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='migrationTaskCheckTimer' and model ='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('migrationTaskCheckTimer','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='MigrationEverydayProcessTask' and model ='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('MigrationEverydayProcessTask','ufm','0','0',1);
delete from system_job_runtimeinfo where jobName='monitorLocalCacheProducer' and model ='ufm';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('monitorLocalCacheProducer', 'ufm','0', '0', '0');
delete from system_job_runtimeinfo where jobName='monitorLocalCacheProducer' and model ='isystem';
INSERT INTO `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) VALUES ('monitorLocalCacheProducer', 'isystem','0', '0', '0');
delete from system_job_runtimeinfo where jobName='checkCopyRunTimer' and model ='isystem';
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('checkCopyRunTimer','isystem','0','0',1);

delete from system_config where id='mirror.task.exe.timeout.minute';
INSERT INTO system_config VALUES ('user.qos.upload.traffice', '2048', -1);
delete from system_config where id='user.qos.download.traffice';
INSERT INTO system_config VALUES ('user.qos.download.traffice', '2048', -1);
delete from system_config where id='user.qos.concurrent';
INSERT INTO system_config VALUES ('user.qos.concurrent', '20', -1);
delete from system_config where id='securityConfig.protocolType';
INSERT INTO system_config VALUES ('securityConfig.protocolType', 'https', -1);
delete from system_config where id='synchronous.user.max.nodenum';
INSERT INTO system_config VALUES ('synchronous.user.max.nodenum', '10000000', -1);
delete from system_config where id='user.max.file.num';
INSERT INTO system_config VALUES ('user.max.file.num', '10000000', -1);
delete from system_config where id='matrix.security.check';
INSERT INTO system_config VALUES ('matrix.security.check', 'false', -1);
delete from system_config where id='matrix.security.check.engine';
INSERT INTO system_config VALUES ('matrix.security.check.engine', 'standard', -1);
delete from system_config where id='preview.convert.timeout';
INSERT INTO system_config VALUES ('preview.convert.timeout', '1800000', -1);
delete from system_config where id='watermark.max.size';
INSERT INTO system_config VALUES ('watermark.max.size', '2097152', -1);
delete from system_config where id='security.scan.enable';
INSERT INTO system_config VALUES ('security.scan.enable', 'false', -1);
delete from system_config where id='security.scan.timeout.seconds';
INSERT INTO system_config VALUES ('security.scan.timeout.seconds', 300, -1);
delete from system_config where id='security.scan.engine.version';
INSERT INTO system_config VALUES ('security.scan.engine.version', '1', -1);
delete from system_config where id='message.retention.days';
INSERT INTO system_config VALUES ('message.retention.days', '7', -1);
delete from system_config where id='system.inner.loadbalance.enable';
INSERT INTO system_config VALUES ('system.inner.loadbalance.enable', 'true', -1);
delete from system_config where id='system.inner.loadbalance.try.counts';
INSERT INTO system_config VALUES ('system.inner.loadbalance.try.counts', '3', -1);
delete from system_config where id='system.linkcode.security.enable';
INSERT INTO system_config VALUES ('system.linkcode.security.enable', 'true', -1);
delete from system_config where id='system.linkcode.security.byte.size';
INSERT INTO system_config VALUES ('system.linkcode.security.byte.size', '16', -1);
delete from system_config where id='lockcount';
INSERT INTO system_config VALUES ('lockcount', '5', -1);
delete from system_config where id='lockperiod';
INSERT INTO system_config VALUES ('lockperiod', '86400', -1);
delete from system_config where id='locktime';
INSERT INTO system_config VALUES ('locktime', '300', -1);
delete from system_config where id='nearestStore.Status';
INSERT INTO system_config VALUES ('nearestStore.Status', '0', -1);
delete from system_config where id='mirror.task.exe.timeout.minute';
INSERT INTO system_config VALUES ('mirror.task.exe.timeout.minute', '1440', -1);
delete from system_config where id='mirror.task.send.timeout.minute';
INSERT INTO system_config VALUES ('mirror.task.send.timeout.minute', '60', -1);
delete from system_config where id='mirror.global.enable';
INSERT INTO system_config VALUES ('mirror.global.enable', 'false', -1);
delete from system_config where id='mirror.global.task.state';
INSERT INTO system_config VALUES ('mirror.global.task.state', '0', -1);
delete from system_config where id='mirror.global.policy.statistic.enable';
INSERT INTO system_config VALUES ('mirror.global.policy.statistic.enable', 'false', -1);
delete from system_config where id='mirror.global.policy.statistic.type';
INSERT INTO system_config VALUES ('mirror.global.policy.statistic.type', '1', -1);
delete from system_config where id='mirror.global.task.delete.code';
INSERT INTO system_config VALUES ('mirror.global.task.delete.code', '404,409,503', -1);
delete from system_config where id='mirror.global.policy.statistic.timeout.minute';
INSERT INTO system_config VALUES ('mirror.global.policy.statistic.timeout.minute', '180', -1);
delete from system_config where id='system.near.access.enable';
INSERT INTO system_config VALUES ('system.near.access.enable', 'false', -1);
delete from system_config where id='mailServer.mailSecurity';
INSERT INTO system_config VALUES ('mailServer.mailSecurity', 'tls', -1);
delete from system_config where id='user.data.migration.task.max';
INSERT INTO system_config VALUES ('user.data.migration.task.max', '100', -1);
delete from system_config where id='user.data.migration.task.retain.days';
INSERT INTO system_config VALUES ('user.data.migration.task.retain.days', '30', -1);
delete from system_config where id='user.migration.task.scan.timeout.second';
INSERT INTO system_config VALUES ('user.migration.task.scan.timeout.second', '1200', -1);
delete from system_config where id='mirror.use.slavedb';
INSERT INTO system_config VALUES ('mirror.use.slavedb', 'false', -1);
delete from system_config where id='mirror.global.enable.timer';
INSERT INTO system_config VALUES ('mirror.global.enable.timer', 'true', -1);
delete from system_config where id='timeconfig.enable';
INSERT INTO system_config VALUES ('timeconfig.enable', 'false', -1);

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


DROP TABLE IF EXISTS `waiting_delete_object`;
CREATE TABLE `waiting_delete_object` (
  `objectId` varchar(32) NOT NULL,
  `resourceGroupId` int(11) NOT NULL,
  `createdAt` datetime DEFAULT NULL,
  KEY `idx_objectId` (`objectId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


use sysdb;
DROP TABLE IF EXISTS `filelabel`;
CREATE TABLE `filelabel`(
		id BIGINT(20) NOT NULL COMMENT '主鍵',
		labelName VARCHAR(128) NOT NULL COMMENT '标签名',
		enterpriseId BIGINT(20) NOT NULL COMMENT '企業賬戶',
		labelType int NOT NULL COMMENT '標簽類型',
		createBy BIGINT(20) NOT NULL COMMENT '创建者',
		createTime datetime COMMENT '創建時間',
		bindedTimes BIGINT(20) NOT NULL default 0 COMMENT '綁定次數',
		PRIMARY KEY (`id`),
		UNIQUE KEY `uq_enterprise_label_key` (`enterpriseId`,`labelName`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
