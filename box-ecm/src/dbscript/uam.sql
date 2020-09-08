/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50529
Source Host           : localhost:3306
Source Database       : uam

Target Server Type    : MYSQL
Target Server Version : 50529
File Encoding         : 65001

Date: 2014-04-15 11:50:32
*/

create database if not exists `uam`;

use `uam`;

SET FOREIGN_KEY_CHECKS=0;

drop table if exists db_version;
CREATE TABLE `db_version` (                            
    `version` varchar(128) NOT NULL,
    `modified` date NOT NULL,                            
    `description` varchar(2048) NOT NULL,                
    `sequence` int default NULL,          
    PRIMARY KEY (`version`),
    KEY `sequence` (`sequence`)                                     
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
insert into db_version(version, modified, description) values('1.3.01.1401', '2015-5-30', '');

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

-- ----------------------------
-- Table structure for `admin`
-- ----------------------------
DROP TABLE IF EXISTS `admin`;

CREATE TABLE `admin` (
  `id` bigint(20) NOT NULL,
  `enterpriseId` bigint(20) DEFAULT '0',
  `type` tinyint(4) NOT NULL,
  `domainType` tinyint(4) NOT NULL,
  `roles` varchar(255) NOT NULL,
  `objectSid` varchar(255) DEFAULT NULL,
  `loginName` varchar(64) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `name` varchar(64) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  `lastLoginTime` datetime DEFAULT NULL,
  `validateKey` varchar(2048) DEFAULT NULL,
  `dynamicPassword` varchar(2048) DEFAULT NULL,
  `noteDesc` varchar(255) DEFAULT NULL,
  `status` tinyint(1) DEFAULT '1',
  `iterations` int(10) NOT NULL,
  `salt` varchar(255) NOT NULL,
  `resetPasswordAt` datetime DEFAULT NULL,
  `lastLoginIP` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_admin_entepriseId_loginName` (`enterpriseId`,`loginName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO admin VALUES ('0', '0', '-1', '1', 'ADMIN_MANAGER', '', 'admin', 'bc9b542a5f280c1b5506d12a4494c371a0075fbecba88f1938cddc6b13765ed408c0b5ccabfff4ca550fce7ad06a24982bbd4906adc3575f9792e6f2b1c130e1', 'Administrator', '', '2014-04-15 11:50:18', '2014-04-15 11:50:18', null, null, null,null,'1',1000,'5b4240346266653662',null,null);

-- ----------------------------
-- Table structure for `admin_log`
-- ----------------------------
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
  `appId` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `adminId` (`adminId`),
  KEY `adminLoginName` (`adminLoginName`),
  KEY `adminShowName` (`adminShowName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for user_log
-- ----------------------------
DROP TABLE IF EXISTS `user_log`;
CREATE TABLE `user_log` (
  `id` bigint(20) NOT NULL,
  `logid` varchar(128) default NULL,
  `userId` bigint(20) default NULL,
  `userLoginName` varchar(50) default NULL,
  `operateType` int(11) default NULL,
  `operateDeviceType` int(11) default NULL,
  `deviceId` varchar(50) default NULL,
  `agentType` varchar(50) default NULL,
  `agentVersion` varchar(100) default NULL,
  `deviceIp` varchar(32) default NULL,
  `operateObjectId` varchar(50) default NULL,
  `operateTime` datetime NOT NULL,
  `operateObjectOwnUser` varchar(50) default NULL,
  `operateObjectTargetUser` varchar(50) default NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `userId` (`userId`),
  KEY `userLoginName` (`userLoginName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `user_ldap`;
CREATE TABLE `user_ldap` (
  `id` varchar(40) NOT NULL,
  `sessionId` varchar(255) NOT NULL,
  `dn` varchar(2048) NOT NULL,
  `loginName` varchar(64) NOT NULL,
  `authServerId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user` (`loginName`,`authServerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ----------------------------
-- Table structure for `authapp`
-- ----------------------------
DROP TABLE IF EXISTS `authapp`;
CREATE TABLE `authapp` (
  `authAppId` varchar(64) NOT NULL,
  `ufmAccessKeyId` varchar(128) NOT NULL,
  `ufmSecretKey` varchar(2048) NOT NULL,
  `ufmSecretKeyEncodeKey` varchar(2048) NOT NULL,
  `name` varchar(64) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  `authConfigId` bigint(20) DEFAULT NULL,
  `mailServerConfigId` bigint(20) DEFAULT NULL,
  `description` varchar(512) DEFAULT NULL,
  `type` tinyint(4) NOT NULL,
  `networkRegionStatus` tinyint(1) DEFAULT 0,
  UNIQUE KEY `authAppId_UNIQUE` (`authAppId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `authapp_permission`;
CREATE TABLE `authapp_permission` (
  `adminId` int(20) NOT NULL,
  `appId` varchar(64) NOT NULL,
  PRIMARY KEY  (`adminId`,`appId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of authapp
-- ----------------------------

-- ----------------------------
-- Table structure for `authserver`
-- ----------------------------
DROP TABLE IF EXISTS `authserver`;
CREATE TABLE `authserver` (
  `id` bigint(20) NOT NULL,
  `name` varchar(64) DEFAULT NULL,
  `type` varchar(64) NOT NULL,
  `ldapBasicXml` longtext,
  `filedMappingXml` longtext,
  `syncPolicyXml` longtext,
  `defaultFlag` tinyint(1) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  `appId` varchar(64) DEFAULT NULL,
  `ldapNodesFilterXml` longtext,
  `enterpriseId` bigint(20) DEFAULT 0,
  `description` varchar(255) DEFAULT NULL,
  `status` tinyint(1) DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for `client_manage`
-- ----------------------------
DROP TABLE IF EXISTS `client_manage`;
CREATE TABLE `client_manage` (
  `id` int(10) NOT NULL,
  `type` varchar(64) NOT NULL,
  `fileName` varchar(255) NOT NULL,
  `version` varchar(64) NOT NULL,
  `size` bigint(20) NOT NULL,
  `supportSys` varchar(255) NOT NULL,
  `content` longblob,
  `twoDimCodeImage` blob,
  `releaseDate` datetime NOT NULL,
  `downloadUrl` varchar(255) NOT NULL,
  `versionInfo` blob,
  `appId` varchar(64) DEFAULT NULL,
  `twoDimCodeName` varchar(255) DEFAULT NULL,
  `plistDownloadUrl` varchar(255) DEFAULT NULL,
  `plistFileName` varchar(255) DEFAULT NULL,
  `plistSize` bigint(20) DEFAULT NULL,
  `plistContent` blob,
  `checkCode` varchar(64) NOT NULL,
  `arithmetic` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
-- ----------------------------
-- Records of client_manage
-- ----------------------------

-- ----------------------------
-- Table structure for `mailserver`
-- ----------------------------
DROP TABLE IF EXISTS `mailserver`;
CREATE TABLE `mailserver` (
  `id` bigint(20) NOT NULL,
  `server` varchar(128) NOT NULL,
  `port` int(10) NOT NULL,
  `senderMail` varchar(255) NOT NULL,
  `senderName` varchar(255) NOT NULL,
  `mailSecurity` varchar(255) DEFAULT 'tls' NOT NULL,
  `enableAuth` tinyint(1) DEFAULT '0' COMMENT 'Default 0, not enabled, 1 : enabled',
  `authUsername` varchar(255) DEFAULT NULL,
  `authPassword` varchar(2048) DEFAULT NULL,
  `authPasswordEncodeKey` varchar(2048) DEFAULT NULL,
  `defaultFlag` tinyint(1) NOT NULL DEFAULT '1' COMMENT '0 is the default mail server',
  `appId` varchar(64) DEFAULT NULL,
  `testMail` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `server_port_UNIQUE` (`server`,`port`,`appId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for `system_config`
-- ----------------------------
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config` (
  `appId` varchar(64) NOT NULL,
  `id` varchar(128) NOT NULL,
  `value` varchar(2048) NOT NULL,
  `comment` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`appId`,`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for network_region
-- ----------------------------
DROP TABLE IF EXISTS `network_region`;
CREATE TABLE `network_region` (
  `id` bigint(20) NOT NULL,
  `ipStart` varchar(32) NOT NULL,
  `ipEnd` varchar(32) NOT NULL,
  `ipStartValue` bigint(20) DEFAULT NULL,
  `ipEndValue` bigint(20) DEFAULT NULL,
  `networkType` tinyint(4) NOT NULL,
  `regionId` bigint(20) NOT NULL,
  `regionName` varchar(200) DEFAULT NULL,
  `uploadBandWidth` bigint(20) DEFAULT NULL,
  `downloadBandWidth` bigint(20) DEFAULT NULL,
  `location` varchar(4000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
-- ----------------------------
-- Records of network_region
-- ----------------------------

-- ----------------------------
-- Table structure for `security_permission`
-- ----------------------------
DROP TABLE IF EXISTS `security_permission`;
CREATE TABLE `security_permission` (
  `keyName` varchar(128) NOT NULL,
  `permissionDesc` varchar(128) NOT NULL,
  `apiPath` varchar(200) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for security_matrix
-- ----------------------------
DROP TABLE IF EXISTS `security_matrix`;
CREATE TABLE `security_matrix` (
  `appId` varchar(100) NOT NULL COMMENT 'Application Type Code',
  `userType` varchar(100) NOT NULL COMMENT 'Users type of access',
  `networkType` varchar(100) NOT NULL COMMENT 'Access Network Type',
  `deviceType` varchar(100) NOT NULL COMMENT 'Equipment type',
  `srcResourceType` varchar(100) NOT NULL COMMENT 'Access Resource Type',
  `resTypeCode` varchar(100) NOT NULL COMMENT 'Extension type code',
  `resTypeDesc` varchar(255) NULL COMMENT 'Extended Type Description',
  `roleName` varchar(255) DEFAULT NULL COMMENT 'Role name of the matrix',
  `permissionValue` varchar(2000) DEFAULT NULL COMMENT 'permission Value'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for security_factor
-- ----------------------------
DROP TABLE IF EXISTS `security_factor`;
CREATE TABLE `security_factor` (
  `type` tinyint(11) NOT NULL COMMENT 'type',
  `code` int(11) NOT NULL COMMENT 'serial number',
  `name` varchar(128) NOT NULL COMMENT 'name',
  `description` varchar(1024) NOT NULL COMMENT 'Chinese description',
  `isSystem` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Whether the system has been initialized'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for proxy_network
-- ----------------------------
DROP TABLE IF EXISTS `proxy_network`;
CREATE TABLE `proxy_network` (
  `id` int(10) NOT NULL,
  `accessIp` varchar(16) NOT NULL,
  `netWorkType` int(2) NOT NULL,
  `regionId` tinyint(4) DEFAULT NULL,
  `regionName` varchar(200) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `accessIp_UNIQUE` (`accessIp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ----------------------------
-- Table structure for `system_lock`
-- ----------------------------
DROP TABLE IF EXISTS `system_lock`;
CREATE TABLE `system_lock` (
  `id` varchar(32) NOT NULL,
  `lock` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of system_lock
-- ----------------------------
INSERT INTO system_lock VALUES ('adminId', '1');
INSERT INTO system_lock VALUES ('adminLogId', '1');
INSERT INTO system_lock VALUES ('appId', '1');
INSERT INTO system_lock VALUES ('clientManageId', '1');
INSERT INTO system_lock VALUES ('networkRegionId', '1');
INSERT INTO system_lock VALUES ('permissionId', '1');
INSERT INTO system_lock VALUES ('sharePermissionId', '1');
INSERT INTO system_lock VALUES ('userId', '1');
INSERT INTO system_lock VALUES ('mailServerId', '1');
INSERT INTO system_lock VALUES ('authServerId', '1');
INSERT INTO system_lock VALUES ('userLogId', '1');

-- ----------------------------
-- Table structure for `system_logo`
-- ----------------------------
DROP TABLE IF EXISTS `system_logo`;
CREATE TABLE `system_logo` (
  `id` int(10) NOT NULL DEFAULT '0',
  `domainName` varchar(512) DEFAULT NULL,
  `title` varchar(512) DEFAULT NULL,
  `logo` blob,
  `icon` blob,
  `copyright` varchar(512) DEFAULT NULL,
  `logoFormatName` varchar(32) DEFAULT NULL,
  `iconFormatName` varchar(32) DEFAULT NULL,
  `titleEn` varchar(512) DEFAULT NULL,
  `copyrightEn` varchar(512) DEFAULT NULL,
  `appId` varchar(64) DEFAULT NULL,
  `appEmailTitle` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of system_logo
-- ----------------------------
INSERT INTO system_logo VALUES ('0',null,null,null,null,null,null,null,null,null,null,null);

-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL,
  `objectSid` varchar(96) DEFAULT NULL,
  `loginName` varchar(64) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `department` varchar(255) DEFAULT NULL,
  `departmentCode` varchar(64) DEFAULT NULL,
  `name` varchar(127) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  `lastLoginAt` datetime DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `domain` varchar(45) DEFAULT NULL,
  `cloudUserId` bigint(20) DEFAULT NULL,
  `principalType` tinyint(4) NOT NULL COMMENT 'Contains research and development, non-development, non-confidential research and development',
  `resourceType` int(10) NOT NULL,
  `appId` varchar(64) NOT NULL,
  `regionId` int(11) DEFAULT NULL,
  `maxVersions` int(11) DEFAULT -1,
  `spaceQuota` bigint(20) DEFAULT -1,
  `teamSpaceFlag` tinyint(4) DEFAULT 1,
  `teamSpaceMaxNum` int(11) DEFAULT -1,
  `validateKey` varchar(2048) DEFAULT NULL,
  `iterations` int(10) DEFAULT 1000,
  `salt` varchar(255) DEFAULT NULL,
  `uploadBandWidth` bigint(20) DEFAULT -2,
  `downloadBandWidth` bigint(20) DEFAULT -2,
  PRIMARY KEY (`id`),
  UNIQUE KEY `loginName_appId_UNIQUE` (`loginName`,`appId`),
  UNIQUE KEY `objectSid_appId_UNIQUE` (`objectSid`,`appId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
  `id` varchar(36) NOT NULL,
  `tag` varchar(64) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
DROP TABLE IF EXISTS `user_tag`;
CREATE TABLE `user_tag` (
  `userId` bigint(20) NOT NULL,
  `tagId` varchar(36) NOT NULL,
  PRIMARY KEY  (`userId`,`tagId`),
  KEY `idx_usertag_tag` (`tagId`),
  KEY `idx_usertag_userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
-- ----------------------------
-- Table structure for `user_terminal`
-- ----------------------------
DROP TABLE IF EXISTS `user_terminal_0`;
CREATE TABLE `user_terminal_0` (
  `id` bigint(20) NOT NULL,	
  `accountId` bigint(20) DEFAULT NULL,	
  `deviceSn` varchar(128) DEFAULT NULL,
  `cloudUserId` bigint(20) NOT NULL,
  `deviceName` varchar(128) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `lastAccessAt` datetime DEFAULT NULL,
  `lastAccessIP` varchar(64) DEFAULT NULL,
  `status` tinyint(1) DEFAULT NULL,
  `deviceOS` varchar(255) DEFAULT NULL,
  `deviceType` tinyint(4) DEFAULT NULL,
  `deviceAgent` varchar(128) DEFAULT NULL,
  `appId` varchar(64) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_userId` (`cloudUserId`),
  KEY `idx_sn` (`deviceSn`),
  KEY `idx_sn_userId` (`deviceSn`,`cloudUserId`),
  KEY `idx_userId_type_addr_agent` (`deviceType`,`deviceAgent`,`lastAccessIP`,`cloudUserId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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

insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ( 'autoRecoverTask','uam','autoRecoverTask',1,'autoRecoverTask','',0,'0 0/1 * * * ?','100','1',-1,-1,'1','');
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ( 'ldapDomainServerCheck','uam','ldapDomainServerCheck',1,'ldapAuthServiceManager','',1,'0 0/10 * * * ?','100','1',-1,-1,'1','');
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ( 'mailServerCheck','uam','mailServerCheck',1,'mailServerServiceImpl','',1,'0 0/10 * * * ?','100','1',-1,-1,'1','');
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ( 'UserLogCreateTableJob','uam','UserLogCreateTableJob',1,'createUserLogTablesTask','20',1,'0 0 1 * * ?','100','1',-1,-1,'1','');
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ( 'systemStatTask','uam','systemStatTask',1,'systemStatTask','',1,'0 1 0 * * ?','100','1',-1,-1,'1','');
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ( 'concurrentCheckTask','uam','concurrentCheckTask',1,'tomcatMonitor','',0,'0 * * * * ?','100','1',-1,-1,'1','');
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ( 'deleteLdapTempUserTask','uam','deleteLdapTempUserTask',1,'deleteLdapTempUser','',1,'0 0/30 * * * ?','100','1',-1,-1,'1','');
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('clearRedundantJobExecuteRecordJob','uam','clearRedundantJobExecuteRecordJobForUAM',1,'clearRedundantJobExecuteRecordJob','',1,'15 1 0/1 * * ?','100','1',-1,-1,'1','');
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('clearRedundantJobExecuteRecordJob','bms','clearRedundantJobExecuteRecordJobForBMS',1,'clearRedundantJobExecuteRecordJob','',1,'15 1 0/1 * * ?','100','1',-1,-1,'1','');
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('clearRedundantJobExecuteRecordJob','cloudapp','clearRedundantJobExecuteRecordJobForCloudapp',1,'clearRedundantJobExecuteRecordJob','',1,'15 1 0/1 * * ?','100','1',-1,-1,'1','');
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('expiredMsgCleanTask','uam','expiredMsgCleanTask',1,'expiredMsgCleanTask','10',1,'1 1 1 * * ?','100','1',-1,-1,'1','');
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('terminalStatisticsJob','uam','terminalStatisticsJob',1,'terminalStatisticsJob','',1,'0 30 0 * * ?','100','1',-1,-1,'1','');
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ( 'UserLoginLogCreateTableJob','uam','UserLoginLogCreateTableJob',1,'userLoginLogCreateTablesTask','5',1,'0 0 1 * * ?','100','1',-1,-1,'1','');
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ( 'UserLoginLogDropTableJob','uam','UserLoginLogDropTableJob',1,'userLoginLogDropTablesTask','10',1,'0 0 5 * * ?','100','1',-1,-1,'1','');
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('bmsNetworkCheckJob','bms','bmsNetworkCheckJob',1,'bmsNetworkCheckJob','5',0,'0/15 * * * * ?','100','1',-1,-1,'1','');
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('cloudappNetworkCheckJob','cloudapp','cloudappNetworkCheckJob',1,'cloudappNetworkCheckJob','5',0,'0/15 * * * * ?','100','1',-1,-1,'1','');
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('uamNetworkCheckJob','uam','uamNetworkCheckJob',1,'uamNetworkCheckJob','5',0,'0/15 * * * * ?','100','1',-1,-1,'1','');
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('feedBackDeleteJob','bms','feedBackDeleteJob',1,'feedBackDeleteJob','',0,'0 0 0 L 1/3 ? ','100','1',-1,-1,'1','');
INSERT INTO `system_job_def` VALUES ('wxUserCleanInvitJob', 'uam', 'wxUserCleanInvitJob', '1', 'wxUserCleanInvitJob', null, '1', '0 0 0 * * ?', '100', '1', '-1', '-1', '1', null);
INSERT INTO `system_job_def` VALUES ('userProfitDetailTask', 'uam', 'userProfitDetailTask', '1', 'userProfitDetailTask', null, '1', '0 */5 * * * ?', '100', '1', '-1', '-1', '1', null);



insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('autoRecoverTask','uam','0','0',1);
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('ldapDomainServerCheck','uam','0','0',1);
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('mailServerCheck','uam','0','0',1);
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('UserLogCreateTableJob','uam','0','0',1);
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('systemStatTask','uam','0','0',1);
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('concurrentCheckTask','uam','0','0',1);
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('deleteLdapTempUserTask','uam','0','0',1);
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('clearRedundantJobExecuteRecordJob','uam','0','0',1);
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('clearRedundantJobExecuteRecordJob','bms','0','0',1);
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('clearRedundantJobExecuteRecordJob','cloudapp','0','0',1);
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('expiredMsgCleanTask','uam','0','0',1);
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) 
	values ('terminalStatisticsJob','uam','0','0',1);
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) 
	values ('UserLoginLogCreateTableJob','uam','0','0',1);
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) 
	values ('UserLoginLogDropTableJob','uam','0','0',1);
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) 
	values ('bmsNetworkCheckJob','bms','0','0',1);
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) 
	values ('cloudappNetworkCheckJob','cloudapp','0','0',1);
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) 
	values ('uamNetworkCheckJob','uam','0','0',1);
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) 
	values ('feedBackDeleteJob','bms','0','0',1);



-- ----------------------------
-- Table structure for user_special
-- ----------------------------
DROP TABLE IF EXISTS `user_special`;
CREATE TABLE `user_special` (
  `userName` varchar(50) NOT NULL,
  `specialType` int(11) NOT NULL,
  PRIMARY KEY  (`userName`,`specialType`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `event_log`;
CREATE TABLE `event_log` (
  `id` varchar(32) primary key,
  `userId` bigint(20) NOT NULL,
  `type` varchar(50) NOT NULL,
  `createdAt` datetime NOT NULL,
  `clientDeviceType` varchar(50),
  `clientDeviceSN` varchar(50),
  `clientAgent` varchar(50),
  `clientAddress` varchar(32),
  `sourceId` bigint(20),
  `sourceParentId` bigint(20),
  `sourceOwnerId` bigint(20),
  `sourceName` varchar(255),
  `sourceType` tinyint(4),
  `destId` bigint(20),
  `destParentId` bigint(20),
  `destOwnerId` bigint(20),
  `destName` varchar(255),
  `destType` tinyint(4),
  KEY `idx_userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `system_stat`;
CREATE TABLE `system_stat` (
  `statDate` datetime NOT NULL,
  `createDate` datetime DEFAULT NULL,
  `totalUser` bigint(20) DEFAULT NULL,
  `loginUserCount` bigint(20) DEFAULT NULL,
  `webAccessAgentCount` bigint(20) DEFAULT NULL,
  `pcAccessAgentCount` bigint(20) DEFAULT NULL,
  `androidAccessCount` bigint(20) DEFAULT NULL,
  `iosAccessCount` bigint(20) DEFAULT NULL,
  `appId` varchar(64) CHARACTER SET latin1 NOT NULL,
  PRIMARY KEY (`statDate`,`appId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
-- ----------------------------
-- Procedure structure for `get_next_id`
-- ----------------------------
DROP PROCEDURE IF EXISTS `get_next_id`;
DELIMITER ;;
CREATE PROCEDURE `get_next_id`(in param varchar(32), out returnid bigint)
    SQL SECURITY INVOKER
BEGIN
  update system_lock set `lock`=`lock`+1 where id = param;
  select `lock` into returnid from system_lock where id = param;
END
;;
DELIMITER ;

DROP PROCEDURE IF EXISTS create_user_terminal_sub_table;
DELIMITER $$
CREATE PROCEDURE create_user_terminal_sub_table()
SQL SECURITY INVOKER
BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 1;
    WHILE i<100 DO
        SET aa=CONCAT('user_terminal_',i);
        SET @createobjfiles= CONCAT(' create table ',aa ,' like user_terminal_0');
        PREPARE stmt FROM @createobjfiles;
        EXECUTE stmt;
        SET i=i+1;
    END WHILE;
END $$
DELIMITER ;
CALL  create_user_terminal_sub_table;

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
CONSTRAINT FK_QRTZ_TRIGGERS FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP) REFERENCES QRTZ_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP))
ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE QRTZ_SIMPLE_TRIGGERS (
SCHED_NAME VARCHAR(120) NOT NULL,
TRIGGER_NAME VARCHAR(200) NOT NULL,
TRIGGER_GROUP VARCHAR(200) NOT NULL,
REPEAT_COUNT BIGINT(7) NOT NULL,
REPEAT_INTERVAL BIGINT(12) NOT NULL,
TIMES_TRIGGERED BIGINT(10) NOT NULL,
PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
CONSTRAINT FK_QRTZ_SIMPLE_TRIGGERS FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP) REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))
ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE QRTZ_CRON_TRIGGERS (
SCHED_NAME VARCHAR(120) NOT NULL,
TRIGGER_NAME VARCHAR(200) NOT NULL,
TRIGGER_GROUP VARCHAR(200) NOT NULL,
CRON_EXPRESSION VARCHAR(120) NOT NULL,
TIME_ZONE_ID VARCHAR(80),
PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
CONSTRAINT FK_QRTZ_CRON_TRIGGERS FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP) REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))
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
    CONSTRAINT FK_QRTZ_SIMPROP_TRIGGERS FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP) REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))
ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE QRTZ_BLOB_TRIGGERS (
SCHED_NAME VARCHAR(120) NOT NULL,
TRIGGER_NAME VARCHAR(200) NOT NULL,
TRIGGER_GROUP VARCHAR(200) NOT NULL,
BLOB_DATA BLOB NULL,
PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
INDEX (SCHED_NAME,TRIGGER_NAME, TRIGGER_GROUP),
CONSTRAINT FK_QRTZ_BLOB_TRIGGERS FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP) REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))
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

-- ----------------------------
-- Table structure for excel_importresult
-- ----------------------------
DROP TABLE IF EXISTS `excel_importresult`;
CREATE TABLE `excel_importresult` (
  `id` varchar(40),
  `errData` longblob,
  `runtime` datetime DEFAULT NULL,
  `completeTime` datetime DEFAULT NULL,
  `status` int(11) NOT NULL DEFAULT '3',
  `enterpriseId` bigint(20) NOT NULL,
  `authServerId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `enterprise`;
CREATE TABLE `enterprise` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `contactPerson` varchar(255) DEFAULT NULL,
  `contactPhone` varchar(255) DEFAULT NULL,
  `contactEmail` varchar(255) NOT NULL,
  `domainName` varchar(64) NOT NULL,
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  `status` tinyint(1) DEFAULT '1' COMMENT '1, normal 2, disabled',
  `networkAuthStatus` tinyint(1) DEFAULT '2' COMMENT '1,enabled, 2 disabled',
  `maxMembers` int(20) DEFAULT 0,
  `description` varchar(255) DEFAULT NULL,
  `ownerId` bigint(20) DEFAULT '-1',
  `pwdLevel` varchar(20) DEFAULT NULL,
  `isdepartment` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_enterprise_name` (`name`),
  UNIQUE KEY `idx_enterprise_domainName` (`domainName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `enterprise_user_0`;
CREATE TABLE `enterprise_user_0` (
  `id` bigint(20) NOT NULL,
  `enterpriseId` bigint(20) NOT NULL,
  `departmentId` bigint(20) NOT NULL DEFAULT '-1',
  `name` varchar(127) NOT NULL,
  `staffNo` varchar(20) DEFAULT NULL,
  `staffSecretLevel` tinyint(1) DEFAULT '0',
  `role` tinyint(1) NOT NULL DEFAULT '2',
  `objectSid` varchar(255) NOT NULL,
  `alias` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `mobile` varchar(20) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  `lastLoginAt` datetime DEFAULT NULL,
  `status` tinyint(1) DEFAULT '0',
  `ldapStatus` tinyint(1) DEFAULT '1',
  `userSource` bigint(20) DEFAULT '0',
  `type` tinyint(1) DEFAULT NULL,
  `validateKey` varchar(1024) DEFAULT NULL,
  `validateKeyEncodeKey` varchar(1024) DEFAULT NULL,
  `password` varchar(1024) DEFAULT NULL,
  `iterations` int(11) DEFAULT '1000',
  `salt` varchar(255) DEFAULT NULL,
  `resetPasswordAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_enterpriseId_name` (`enterpriseId`,`name`),
  UNIQUE KEY `idx_enterpriseId_userId` (`enterpriseId`,`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `logininfo_0`;
CREATE TABLE `logininfo_0` (
  `loginName` varchar(64) NOT NULL,
  `enterpriseId` bigint(20) NOT NULL,
  `userId` bigint(20) NOT NULL,
  `loginType` tinyint(1) DEFAULT 1 COMMENT '1,login name, 2 mailbox ,3 phone',
  `domainName` varchar(64) DEFAULT NULL,
  UNIQUE KEY `logininfo_UNIQUE` (`loginName`, `domainName`),
  UNIQUE KEY `logininfo_UNIQUE_1` (`userId`,`loginType`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `enterprise_account`;
CREATE TABLE `enterprise_account` (
  `enterpriseId` bigint(20) NOT NULL,
  `authAppId` varchar(64) NOT NULL,
  `accountId` bigint(20) NOT NULL,
  `status` tinyint(1) DEFAULT 1,
  `accessKeyId` varchar(128) NOT NULL,
  `secretKey` varchar(2048) NOT NULL,
  `secretKeyEncodeKey` varchar(2048) NOT NULL,
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  `maxSpace` bigint(20) DEFAULT '999999999999',
  `maxMember` int(11) DEFAULT '99999999',
  `maxFiles` bigint(20) DEFAULT '99999999',
  `maxTeamspace` int(11) DEFAULT '99999999',
  `filePreviewable` tinyint(1) DEFAULT '0',
  `fileScanable` tinyint(1) DEFAULT '0',
  PRIMARY KEY  (`accountId`),
  UNIQUE KEY `enterprise_account_UNIQUE` (`enterpriseId`,`authAppId`),
  UNIQUE KEY `accessKeyId_UNIQUE` (`accessKeyId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `user_account_0`;
CREATE TABLE `user_account_0` (
  `id` bigint(20) NOT NULL,
  `userid` bigint(20) NOT NULL,
  `enterpriseId` bigint(20) NOT NULL,
  `accountId` bigint(20) NOT NULL,
  `cloudUserId` bigint(20) NOT NULL,
  `imAccount` varchar(128)DEFAULT NULL,
  `status` tinyint(1) DEFAULT 0 COMMENT '0 enabled ,1 disabled',
  `accessKeyId` varchar(128)DEFAULT NULL,
  `secretKey` varchar(128) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  `lastLoginAt` datetime DEFAULT NULL,
  `firstLogin` tinyint(4) DEFAULT NULL,
  `principalType` tinyint(4) DEFAULT NULL COMMENT 'Contains research and development, non-development, non-confidential research and development',
  `resourceType` int(10) DEFAULT NULL,
  `regionId` int(11) DEFAULT '-1',
  `maxVersions` int(11) DEFAULT '-1',
  `spaceQuota` bigint(20) DEFAULT '-1',
  `teamSpaceFlag` tinyint(4) DEFAULT '1',
  `teamSpaceQuota` bigint(20) DEFAULT '-1',
  `teamSpaceMaxNum` int(11) DEFAULT '-1',
  `uploadBandWidth` bigint(20) DEFAULT '-2',
  `downloadBandWidth` bigint(20) DEFAULT '-2',
  `roleId` bigint(20) DEFAULT 0,
  `versionFileSize` bigint NOT NULL DEFAULT -1,
  `versionFileType`  varchar(256) NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `user_account_UNIQUE_1` (`userid`,`accountId`),
  UNIQUE KEY `user_account_UNIQUE_2` (`cloudUserId`,`accountId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `account_authserver`;
CREATE TABLE `account_authserver` (
  `authServerId` bigint(20) NOT NULL,
	`accountId` bigint(20) NOT NULL,
	`ipStart` varchar(32) DEFAULT NULL,
	`ipEnd` varchar(32) DEFAULT NULL,
	`ipStartValue` bigint(20) DEFAULT -1,
	`ipEndValue` bigint(20) DEFAULT -1,
	`type` tinyint(1) DEFAULT 1,
  UNIQUE KEY `account_authserver_UNIQUE` (`authServerId`,`accountId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `account_authserver_network`;
CREATE TABLE `account_authserver_network` (
  `id` bigint(20) NOT NULL,
  `authServerId` bigint(20) NOT NULL,
	`accountId` bigint(20) NOT NULL,
	`ipStart` varchar(32) NOT NULL,
	`ipEnd` varchar(32) NOT NULL,
	`ipStartValue` bigint(20) NOT NULL,
	`ipEndValue` bigint(20) NOT NULL,
	PRIMARY KEY  (`id`),
  KEY `account_authserver_network_UNIQUE` (`authServerId`,`accountId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement` (
  `id` bigint(20) NOT NULL PRIMARY KEY,
  `title` varchar(255) NOT NULL,
  `content` text DEFAULT NULL,
  `publisherId` bigint(1) NOT NULL,
  `publishTime` datetime NOT NULL,
  `topTime` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `system_message`;
CREATE TABLE `system_message` (
  `id` bigint(20) NOT NULL PRIMARY KEY,
  `providerId` bigint(20) NOT NULL,
  `params` text DEFAULT NULL,
  `createdAt` datetime NOT NULL,
  `expiredAt` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `ldap_check`;
CREATE TABLE `ldap_check` (
  `enterpriseId` bigint(20) NOT NULL PRIMARY KEY,
  `lastCheckTime` datetime DEFAULT NULL,
  `checkStatus` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `system_message_status_0`;
CREATE TABLE `system_message_status_0` (
  `messageId` bigint(20) NOT NULL,
  `receiverId` bigint(20) NOT NULL,
  `status` tinyint(4) NOT NULL,
  `expiredAt` datetime NOT NULL,
  PRIMARY KEY `UNIQUE_receiverId_messageId` (`receiverId`,`messageId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Procedure structure for proc_create_sub_message_status
-- ----------------------------
DROP PROCEDURE IF EXISTS proc_create_sub_message_status;
DELIMITER $$
CREATE PROCEDURE proc_create_sub_message_status()
sql security invoker
BEGIN
    declare tableName varchar(256);
    declare i int default 1;
    while i<10 do
        set tableName=CONCAT('system_message_status_',i);
        set @dropSql= CONCAT('DROP TABLE IF EXISTS ',tableName);
        set @createSql= CONCAT('create table ',tableName ,' like system_message_status_0');
        prepare stmt from @dropSql;
        execute stmt;
        prepare stmt from @createSql;
        execute stmt;
        set i=i+1;
    end while;
END $$
DELIMITER ;
CALL  proc_create_sub_message_status;
DROP PROCEDURE IF EXISTS proc_create_sub_message_status;

DROP PROCEDURE IF EXISTS delete_user_account_sub_table;
DELIMITER $$
CREATE PROCEDURE delete_user_account_sub_table()
SQL SECURITY INVOKER
BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 1;
    WHILE i<100 DO
        SET aa=CONCAT('user_account_',i);
        SET @createobjfiles= CONCAT(' DROP TABLE IF EXISTS ',aa);
        PREPARE stmt FROM @createobjfiles;
        EXECUTE stmt;
        SET i=i+1;
    END WHILE;
END $$
DELIMITER ;
CALL  delete_user_account_sub_table;

DROP PROCEDURE IF EXISTS create_user_account_sub_table;
DELIMITER $$
CREATE PROCEDURE create_user_account_sub_table()
SQL SECURITY INVOKER
BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 1;
    WHILE i<100 DO
        SET aa=CONCAT('user_account_',i);
        SET @createobjfiles= CONCAT(' create table ',aa ,' like user_account_0');
        PREPARE stmt FROM @createobjfiles;
        EXECUTE stmt;
        SET i=i+1;
    END WHILE;
END $$
DELIMITER ;
CALL  create_user_account_sub_table;

DROP PROCEDURE IF EXISTS delete_enterprise_user_sub_table;
DELIMITER $$
CREATE PROCEDURE delete_enterprise_user_sub_table()
SQL SECURITY INVOKER
BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 1;
    WHILE i<100 DO
        SET aa=CONCAT('enterprise_user_',i);
        SET @createobjfiles= CONCAT(' DROP TABLE IF EXISTS ',aa);
        PREPARE stmt FROM @createobjfiles;
        EXECUTE stmt;
        SET i=i+1;
    END WHILE;
END $$
DELIMITER ;
CALL  delete_enterprise_user_sub_table;

DROP PROCEDURE IF EXISTS create_enterprise_user_sub_table;
DELIMITER $$
CREATE PROCEDURE create_enterprise_user_sub_table()
SQL SECURITY INVOKER
BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 1;
    WHILE i<100 DO
        SET aa=CONCAT('enterprise_user_',i);
        SET @createobjfiles= CONCAT(' create table ',aa ,' like enterprise_user_0');
        PREPARE stmt FROM @createobjfiles;
        EXECUTE stmt;
        SET i=i+1;
    END WHILE;
END $$
DELIMITER ;
CALL  create_enterprise_user_sub_table;

DROP PROCEDURE IF EXISTS delete_logininfo_sub_table;
DELIMITER $$
CREATE PROCEDURE delete_logininfo_sub_table()
SQL SECURITY INVOKER
BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 1;
    WHILE i<100 DO
        SET aa=CONCAT('logininfo_',i);
        SET @createobjfiles= CONCAT(' DROP TABLE IF EXISTS ',aa);
        PREPARE stmt FROM @createobjfiles;
        EXECUTE stmt;
        SET i=i+1;
    END WHILE;
END $$
DELIMITER ;
CALL  delete_logininfo_sub_table;

DROP PROCEDURE IF EXISTS create_logininfo_sub_table;
DELIMITER $$
CREATE PROCEDURE create_logininfo_sub_table()
SQL SECURITY INVOKER
BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 1;
    WHILE i<100 DO
        SET aa=CONCAT('logininfo_',i);
        SET @createobjfiles= CONCAT(' create table ',aa ,' like logininfo_0');
        PREPARE stmt FROM @createobjfiles;
        EXECUTE stmt;
        SET i=i+1;
    END WHILE;
END $$
DELIMITER ;
CALL  create_logininfo_sub_table;


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

-- ----------------------------
-- Table statistics_temp_chart
-- in cluster environment to store temporary JFreeChart 
-- ----------------------------
DROP TABLE IF EXISTS statistics_temp_chart;
CREATE TABLE statistics_temp_chart (
   `id` varchar(64),
   `createdAt` datetime,    
   `chartImage` MediumBlob,
   PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

DROP PROCEDURE IF EXISTS proc_create_sub_message_status;

-- ----------------------------
-- utilize Quartz framework to clean file folder that store temporary JFreeCharts 
-- three days per clearance 
-- ----------------------------
INSERT INTO `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) VALUES ( 'clearTempStatisChartJob','uam','clearTempStatisChartJob',1,'clearTempStatisChartJob','',1,'0 0 21 */3 * ?','100','1',-1,-1,'1','');
INSERT INTO `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) VALUES ('clearTempStatisChartJob','uam','0','0',1);



DROP TABLE IF EXISTS `access_config`;
CREATE TABLE `access_config` (
  `id` bigint(20) NOT NULL,
  `accountId` bigint(20) NOT NULL,
  `safeRoleId` bigint(20) DEFAULT NULL,
  `spaceRoleId` bigint(20) DEFAULT NULL,
  `netRegionId` bigint(20) DEFAULT NULL,
  `clientType` bigint(20) DEFAULT NULL,
  `downLoadResrouceTypeIds` varchar(128) DEFAULT NULL,
  `previewResourceTypeIds` varchar(128) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `access_config_union_index` (`accountId`,`safeRoleId`,`spaceRoleId`, `netRegionId`,`clientType`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `network_region_excel_importresult`;
CREATE TABLE `network_region_excel_importresult` (
  `id` int(11) NOT NULL,
  `accountId` bigint(20) NOT NULL,
  `resultData` longblob,
  `runtime` datetime DEFAULT NULL,
  `completeTime` datetime DEFAULT NULL,
  `succeededCount` int(11) DEFAULT NULL,
  `failedCount` int(11) DEFAULT NULL,
  `totalCount` int(11) DEFAULT NULL,
  `status` int(11) NOT NULL,
  `appId` varchar(64) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `net_region`;
CREATE TABLE `net_region` (
  `id` bigint(20) NOT NULL,
  `accountId` bigint(20) NOT NULL,
  `netRegionName` varchar(128) DEFAULT NULL,
  `netRegionDesc` varchar(255) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `resource_security_level`;
CREATE TABLE `resource_security_level` (
  `id` bigint(20) NOT NULL,
  `accountId` bigint(20) NOT NULL,
  `safeLevelName` varchar(128) DEFAULT NULL,
  `safeLevelDesc` varchar(255) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `resource_security_level_unique_index` (`safeLevelName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `resource_strategy`;
CREATE TABLE `resource_strategy` (
  `id` bigint(20) NOT NULL,
  `accountId` bigint(20) NOT NULL,
  `netRegionId` bigint(20) DEFAULT NULL,
  `securityRoleId` bigint(20) DEFAULT NULL,
  `resourceSecurityLevelId` bigint(20) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `resource_strategy_union_index` (`accountId`,`netRegionId`,`securityRoleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `security_role`;
CREATE TABLE `security_role` (
  `id` bigint(20) NOT NULL,
  `accountId` bigint(20) NOT NULL,
  `roleName` varchar(128) DEFAULT NULL,
  `roleDesc` varchar(255) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `network_region_config`;
CREATE TABLE `network_region_config` (
  `id` bigint(20) NOT NULL,
  `accountId` bigint(20) NOT NULL,
  `netRegionId` bigint(20) NOT NULL,
  `ipStart` varchar(32) DEFAULT NULL,
  `ipEnd` varchar(32) DEFAULT NULL,
  `ipStartValue` bigint(20) DEFAULT NULL,
  `ipEndValue` bigint(20) DEFAULT NULL,
  `networkTypeId` tinyint(4) DEFAULT NULL,
  `regionId` tinyint(4) DEFAULT NULL,
  `regionName` varchar(200) DEFAULT NULL,
  `uploadBandWidth` bigint(20) DEFAULT NULL,
  `downloadBandWidth` bigint(20) DEFAULT NULL,
  `location` varchar(4000) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

drop table if exists terminal_statistics_day;
CREATE TABLE terminal_statistics_day (
   `day` int NOT NULL,
   `deviceType` tinyint(4) NOT NULL,
   `clientVersion` varchar(128) NOT NULL,
   `userCount` int NOT NULL,   
   primary key (`day`,`deviceType`, `clientVersion`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `account_sec_config`;
CREATE TABLE `account_sec_config` (
  `accountId` bigint(20) NOT NULL,
  `enableSpaceSec` tinyint DEFAULT 0,
  `enableFileSec` tinyint DEFAULT 0,
  `enableFileCopySec` tinyint DEFAULT 0,
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`accountId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


DROP TABLE IF EXISTS `access_space_config`;
CREATE TABLE `access_space_config` (
  `accountId` bigint(20) NOT NULL,
  `id` varchar(40) NOT NULL,
  `safeRoleId` bigint(20) NOT NULL,
  `netRegionId` bigint(20) NOT NULL,
  `targetSafeRoleId` bigint(20) NOT NULL,
  `targetSafeRoleName` varchar(128) DEFAULT NULL,
  `clientType` bigint(20) NOT NULL,
  `operation` bigint(20) NOT NULL,
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `access_config_union_index` (`accountId`,`safeRoleId`,`netRegionId`,`targetSafeRoleId`,`clientType`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- create zookeeperInfo table--
DROP TABLE IF EXISTS `zookeeperInfo`;
CREATE TABLE `zookeeperInfo` (
  `ip` VARCHAR(128) NOT NULL,
  `port` VARCHAR(10) NOT NULL,
  PRIMARY KEY (`ip`,`port`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `account_role`;
CREATE TABLE `account_role` (
  `resourceRole` varchar(255) NOT NULL,
  `accountId` bigint(20) NOT NULL,
  UNIQUE KEY `resourceRole_accountId` (`resourceRole`,`accountId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `account_role` VALUES ('downLoader', '0');
INSERT INTO `account_role` VALUES ('uploadAndView', '0');
INSERT INTO `account_role` VALUES ('editor', '0');
INSERT INTO `account_role` VALUES ('previewer', '0');
INSERT INTO `account_role` VALUES ('uploader', '0');
INSERT INTO `account_role` VALUES ('viewer', '0');

INSERT INTO `system_config` (  `id`,`value`,`appId`) VALUES('watermark.max.size','1048576','-1');

DROP TABLE IF EXISTS `userInterzone`;
CREATE TABLE `userInterzone`(
	`id` varchar(128) NOT NULL,
	`interzone` varchar(128) NOT NULL,
	PRIMARY KEY `PK_USERINTERZONE`(`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `space_copy_config`;
CREATE TABLE `space_copy_config` (
  `id` bigint(40) NOT NULL,
  `srcSafeRoleId` bigint(20) NOT NULL,
  `targetSafeRoleId` bigint(20) NOT NULL,
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  `accountId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `access_config_union_index` (`srcSafeRoleId`,`targetSafeRoleId`,`accountId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `authapp_network_region_config`;
CREATE TABLE `authapp_network_region_config` (
  `id` bigint(20) NOT NULL,
  `authAppId` varchar(64) NOT NULL,
  `ipStart` varchar(32) DEFAULT NULL,
  `ipEnd` varchar(32) DEFAULT NULL,
  `ipStartValue` bigint(20) DEFAULT NULL,
  `ipEndValue` bigint(20) DEFAULT NULL,
  `regionId` tinyint(4) DEFAULT NULL,
  `regionName` varchar(200) DEFAULT NULL,
  `uploadBandWidth` bigint(20) DEFAULT NULL,
  `downloadBandWidth` bigint(20) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY (`authAppId`),
  KEY (`regionId`),
  KEY (`ipStartValue`),
  KEY (`ipEndValue`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


DROP TABLE IF EXISTS `service_node`;
CREATE TABLE `service_node` (
  `regionId` int(11) DEFAULT NULL,
  `dcId` int(11) DEFAULT NULL,
  `clusterId` int(11) NOT NULL,
  `clusterType` varchar(127) NOT NULL,
  `clusterName` varchar(127) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
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


INSERT INTO `system_config` (  `id`,`value`,`appId`) VALUES ('system.inner.loadbalance.enable', 'true', -1);
INSERT INTO `system_config` (  `id`,`value`,`appId`) VALUES ('system.inner.loadbalance.try.counts', '3', -1);
INSERT INTO `system_config` (  `id`,`value`,`appId`) VALUES ('networkRegionIp.excelFile.max.size', '20971520', -1);

INSERT INTO `system_config` (  `id`,`value`,`appId`) VALUES ('system.ufm.innner.https.port', '8443', -1);
INSERT INTO `system_config` (  `id`,`value`,`appId`) VALUES ('system.ufm.innner.http.port', '8080', -1);
INSERT INTO `system_config` (  `id`,`value`,`appId`) VALUES ('lock.time', '5', -1);
INSERT INTO `system_config` (  `id`,`value`,`appId`) VALUES ('lock.failtimes', '5', -1);

DROP TABLE IF EXISTS `user_locked`;
CREATE TABLE `user_locked` (
  `userId` bigint(20) NOT NULL,
  `appId` varchar(64) NOT NULL,
  `loginName` varchar(64) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `lockedAt` datetime DEFAULT NULL,
  `loginFailTimes` int(11) DEFAULT NULL,
  PRIMARY KEY (`userId`,`appId`),
  KEY `appId` (`appId`,`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `cmb_orguser_relation_info_tmp`;
CREATE TABLE `cmb_orguser_relation_info_tmp`(
	`groupId` varchar(16) NOT NULL,
	`orgId` int(11) NOT NULL,
	`userId` varchar(16) NOT NULL,
	`name` varchar(255) NOT NULL,
	`userOrd` int(11) NOT NULL DEFAULT 0,
	unique KEY `uk_orguser_relation_tmp`(`userId`,`orgId`),
	unique KEY `uk_orguser_relation_tmp1`(`userId`,`groupId`),
	KEY `idx_orguser_relation_tmp_orgId`(`orgId`),
	KEY `idx_orguser_relation_tmp_userId`(`userId`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
DROP TABLE IF EXISTS `cmb_user_info_tmp`;
CREATE TABLE `cmb_user_info_tmp`(
	`userId` varchar(16) NOT NULL,
	`name` varchar(255) NOT NULL,
	`gender` int(11) DEFAULT NULL,
	`officeTel` varchar(16) DEFAULT NULL,
	`sapId` varchar(16) NOT NULL,
	`position` varchar(64) DEFAULT NULL,
	`status` varchar(8) NOT NULL,
	`type` int(11) NOT NULL,
	`email` varchar(255) NOT NULL,
	`firstSpell` varchar(16) DEFAULT NULL,
	`spell` varchar(64) DEFAULT NULL,
	unique KEY `uk_user_info_tmp_sapId`(`sapId`),
	unique KEY `uk_user_info_tmp_userId`(`userId`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
DROP TABLE IF EXISTS `cmb_org_info_tmp`;
CREATE TABLE `cmb_org_info_tmp`(
	`orgId` int(11) NOT NULL,
	`groupId` varchar(16) NOT NULL,
	`groupName` varchar(128) NOT NULL,
	`fatherGroupId` varchar(16) DEFAULT NULL,
	`fatherOrgId` int(11) DEFAULT NULL,
	`leaf` varchar(4) DEFAULT NULL,
	`unitType` varchar(4) DEFAULT NULL,
	`hierarchyFlag` varchar(4) DEFAULT NULL,
	`emailId` varchar(255) DEFAULT NULL,
	`headerId` varchar(16) DEFAULT NULL,
	`display` varchar(4) DEFAULT NULL,
	`point` varchar(4) DEFAULT NULL,
	`pathname` varchar(1024) DEFAULT NULL,
	`oneClassOrgId` int(11) DEFAULT NULL,
	`twoClassOrgId` int(11) DEFAULT NULL,
	`threeClassOrgId` int(11) DEFAULT NULL,
	`fourClassOrgId` int(11) DEFAULT NULL,
	`fiveClassOrgId` int(11) DEFAULT NULL,
	`usePlace` varchar(4) DEFAULT NULL,
	`groupProperty` varchar(4) DEFAULT NULL,
	`location` varchar(4) DEFAULT NULL,
	`sortId` varchar(4) DEFAULT NULL,
	`exInfo` varchar(255) DEFAULT NULL,
	unique KEY `uk_org_info_tmp_orgId`(`orgId`),
	unique KEY `uk_org_info_tmp_groupId`(`groupId`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `cmb_orguser_relation_info`;
CREATE TABLE `cmb_orguser_relation_info`(
	`groupId` varchar(16) NOT NULL,
	`orgId` int(11) NOT NULL,
	`userId` varchar(16) NOT NULL,
	`name` varchar(255) NOT NULL,
	`userOrd` int(11) NOT NULL DEFAULT 0,
	`status` tinyint(1) NOT NULL DEFAULT 0,
	unique KEY `uk_orguser_relation`(`userId`,`orgId`),
	unique KEY `uk_orguser_relation_1`(`userId`,`groupId`),
	KEY `idx_orguser_relation_orgId`(`orgId`),
	KEY `idx_orguser_relation_groupId`(`groupId`),
	KEY `idx_orguser_relation_userId`(`userId`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
DROP TABLE IF EXISTS `cmb_user_info`;
CREATE TABLE `cmb_user_info`(
	`userId` varchar(16) NOT NULL,
	`name` varchar(255) NOT NULL,
	`gender` int(11) DEFAULT NULL,
	`officeTel` varchar(16) DEFAULT NULL,
	`sapId` varchar(16) NOT NULL,
	`position` varchar(64) DEFAULT NULL,
	`status` varchar(8) NOT NULL,
	`type` int(11) NOT NULL,
	`email` varchar(255) NOT NULL,
	`firstSpell` varchar(16) DEFAULT NULL,
	`spell` varchar(64) DEFAULT NULL,
	unique KEY `uk_cmb_user_info_sapId`(`sapId`),
	unique KEY `uk_cmb_user_info_userId`(`userId`),
	KEY `idx_cmb_user_info_email`(`email`),
	KEY `idx_cmb_user_info_firstSpell`(`firstSpell`),
	KEY `idx_cmb_user_info_spell`(`spell`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `cmb_org_info`;
CREATE TABLE `cmb_org_info`(
	`orgId` int(11) NOT NULL,
	`groupId` varchar(16) NOT NULL,
	`groupName` varchar(128) NOT NULL,
	`fatherGroupId` varchar(16) DEFAULT NULL,
	`fatherOrgId` int(11) DEFAULT NULL,
	`leaf` varchar(4) DEFAULT NULL,
	`unitType` varchar(4) DEFAULT NULL,
	`hierarchyFlag` varchar(4) DEFAULT NULL,
	`emailId` varchar(255) DEFAULT NULL,
	`headerId` varchar(16) DEFAULT NULL,
	`display` varchar(4) DEFAULT NULL,
	`point` varchar(4) DEFAULT NULL,
	`pathname` varchar(1024) DEFAULT NULL,
	`oneClassOrgId` int(11) DEFAULT NULL,
	`twoClassOrgId` int(11) DEFAULT NULL,
	`threeClassOrgId` int(11) DEFAULT NULL,
	`fourClassOrgId` int(11) DEFAULT NULL,
	`fiveClassOrgId` int(11) DEFAULT NULL,
	`usePlace` varchar(4) DEFAULT NULL,
	`groupProperty` varchar(4) DEFAULT NULL,
	`location` varchar(4) DEFAULT NULL,
	`sortId` varchar(4) DEFAULT NULL,
	`exInfo` varchar(255) DEFAULT NULL,
	unique KEY `uk_org_info_orgId`(`orgId`),
	unique KEY `uk_org_info_groupId`(`groupId`),
	key `idx_org_info_fatherGroupId`(`fatherGroupId`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `account_config`;
CREATE TABLE `account_config` (
  `accountId` bigint(20) NOT NULL,
  `name` varchar(64) NOT NULL,
  `value` varchar(255) NOT NULL,
  UNIQUE KEY `uq_accountId_name` (`accountId`,`name`),
  KEY `accountId` (`accountId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `multi_system_logo`;
CREATE TABLE `multi_system_logo` (
  `accountId` int(20) NOT NULL,
  `titleCh` varchar(512) DEFAULT NULL,
  `titleEn` varchar(512) DEFAULT NULL,
  `webLogo` mediumblob,
  `pcLogo` mediumblob,
  `icon` mediumblob,
  `webLogoFormatName` varchar(32) DEFAULT NULL,
  `pcLogoFormatName` varchar(32) DEFAULT NULL,
  `iconFormatName` varchar(32) DEFAULT NULL,
  `corprightCN` varchar(255) DEFAULT NULL,
  `corprightEN` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`accountId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `user_image_0`;
CREATE TABLE `user_image_0` (
  `userId` int(20) NOT NULL,
  `accountId` int(20) NOT NULL,  
  `userImage` MEDIUMBLOB,
  `imageFormatName` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`userId`,`accountId`)
  
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP PROCEDURE IF EXISTS create_user_image_sub_table;
DELIMITER $$
CREATE PROCEDURE create_user_image_sub_table()
SQL SECURITY INVOKER
BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 1;
    WHILE i<100 DO
        SET aa=CONCAT('user_image_',i);
        SET @createobjfiles= CONCAT(' create table ',aa ,' like user_image_0');
        PREPARE stmt FROM @createobjfiles;
        EXECUTE stmt;
        SET i=i+1;
    END WHILE;
END $$
DELIMITER ;
CALL  create_user_image_sub_table;

DROP TABLE IF EXISTS `user_sign_declare_0`;
CREATE TABLE `user_sign_declare_0` (
  `accountId` bigint(20) NOT NULL,
  `cloudUserId` bigint(20) NOT NULL,
  `userId` bigint(20) NOT NULL,
  `clientType` varchar(64) NOT NULL,
  `isSigned` tinyint(1) DEFAULT '0' COMMENT 'Default 0, not sign, 1 : sign',
  `declareId` varchar(255) DEFAULT NULL,
  PRIMARY KEY `user_sign_declare_unique`(`accountId`,`cloudUserId`,`clientType`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP PROCEDURE IF EXISTS create_sign_declare_sub_table;
DELIMITER $$
CREATE PROCEDURE create_sign_declare_sub_table()
SQL SECURITY INVOKER
BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 1;
    WHILE i<100 DO
        SET aa=CONCAT('user_sign_declare_',i);
        SET @createobjfiles= CONCAT(' create table ',aa ,' like user_sign_declare_0');
        PREPARE stmt FROM @createobjfiles;
        EXECUTE stmt;
        SET i=i+1;
    END WHILE;
END $$
DELIMITER ;
CALL  create_sign_declare_sub_table;

DROP TABLE IF EXISTS `conceal_declare`;
CREATE TABLE `conceal_declare` (
  `id` varchar(255) NOT NULL,
  `appId` varchar(64) NOT NULL,
  `clientType` varchar(64) NOT NULL,
  `createAt` datetime NOT NULL,
  `status` tinyint(1) DEFAULT '0' COMMENT 'Default 1, enabled, 0 : not enabled',
  `declaration` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `multi_system_config`;
CREATE TABLE `multi_system_config` (
  `accountId` int(20) NOT NULL,
  `userSpaceQuota` varchar(255) NOT NULL,
  `userVersions` varchar(255) NOT NULL,
  `enableTeamSpace` varchar(255) NOT NULL,
  `maxTeamSpaces` varchar(255) DEFAULT '0',
  `teamSpaceQuota` varchar(255) DEFAULT '0',
  `teamSpaceVersions` varchar(255) DEFAULT '0',
  `multiVersionContent` varchar(255) DEFAULT '0',
  `maxVersionFileSize` varchar(255) DEFAULT '0',
  PRIMARY KEY (`accountId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `manager_locked`;
CREATE TABLE `manager_locked` (
  `loginName` varchar(64) NOT NULL,
  `createdAt` datetime DEFAULT NULL,
  `lockedAt` datetime DEFAULT NULL,
  `loginFailTimes` int(11) DEFAULT NULL,
  PRIMARY KEY (`loginName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `user_feedback_sub`;
CREATE TABLE `user_feedback_sub` (
  `problemID` varchar(64) DEFAULT NULL,
  `userID` varchar(64) DEFAULT NULL,
  `userName` varchar(64) DEFAULT NULL,
  `description` varchar(2048) DEFAULT NULL,
  `updateTime` datetime DEFAULT NULL,
  `isAnswer` varchar(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `user_feedback_info`;
CREATE TABLE `user_feedback_info` (
  `problemID` int(15) NOT NULL AUTO_INCREMENT,
  `problemType` varchar(1) NOT NULL,
  `problemTitle` varchar(1024) NOT NULL,
  `problemDescription` varchar(2048) DEFAULT NULL,
  `customerID` varchar(64) DEFAULT NULL,
  `customerName` varchar(64) DEFAULT NULL,
  `customerTwTime` datetime DEFAULT NULL,
  `problemStatus` varchar(1) DEFAULT NULL,
  `newestTwTime` datetime DEFAULT NULL,
  `managerAnswerTime` datetime DEFAULT NULL,
  `customerEmail` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`problemID`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8mb4;

alter table enterprise modify contactEmail VARCHAR(255) default null;

use uam;
DROP TABLE IF EXISTS `migration_record_0`;
CREATE TABLE `migration_record_0` (
	`id` BIGINT(20) NOT NULL COMMENT '主鍵',
	`migrationType` INT NOT NULL COMMENT '移交方式',
	`departureCloudUserId` BIGINT(20) NOT NULL COMMENT '离职员工编号',
	`recipientCloudUserId` BIGINT(20) COMMENT '接收员工编号',
	`departureUserId` BIGINT(20) NOT NULL COMMENT '离职员工编号',
	`recipientUserId` BIGINT(20) COMMENT '接收员工编号',
	`migrationStatus` INT NOT NULL COMMENT '移交状态',
	`migrationDate` datetime NOT NULL COMMENT '移交时间',
	`expiredDate` datetime COMMENT '移交文件失效日期',
	`clearDate` datetime COMMENT '清除移交数据时间',
	`inodeId` BIGINT(20) COMMENT '移交文件编号',
	`enterpriseId` BIGINT(20) COMMENT '企业编号',
	`appId` VARCHAR(64) COMMENT '应用编号',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

USE `uam`;
DROP PROCEDURE IF EXISTS migration_record_sub_table;
DELIMITER $$
CREATE PROCEDURE migration_record_sub_table()
sql security invoker
BEGIN
	declare aa varchar(256);
	declare i int default 1;
	while i<100 do
		set aa=CONCAT('migration_record_',i);
		set @createobjfiles= CONCAT(' create table ',aa ,' like migration_record_0');
		prepare stmt from @createobjfiles;
		execute stmt;
		set i=i+1;
	end while;
END $$
DELIMITER ;
CALL  migration_record_sub_table;
DROP PROCEDURE IF EXISTS migration_record_sub_table;

USE `uam`;
INSERT INTO `system_job_def` (`name`, `model`, `description`, `state`, `beanname`, `parameter`, `type`, `cron`, `recordnumber`, `threadnumber`, `datawait`, `clusterjobwait`, `pauseable`, `changeables`)
VALUES( 'cleanDepartureUserInfoJob', 'uam', 'cleanDepartureUserInfoJob', 9, 'cleanDepartureUserInfoJob', '100', 1, 
		'0 0 0 * * ?','100', '1' ,- 1 ,- 1, '1', '');
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) 
	values ('clearDepartureUserInfoJob','uam','0','0',1);
	
/* 组织架构相关 start */
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department` (
  `enterpriseId` bigint(20) NOT NULL,
  `departmentId` bigint(20) NOT NULL,
  `parentId` bigint(20) NOT NULL DEFAULT '0',
  `domain` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `state` tinyint(1) NOT NULL DEFAULT '0',
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`enterpriseId`,`departmentId`),
  KEY `idx_enterprise_parentId` (`enterpriseId`,`parentId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/* 部门对应的虚拟账户*/
DROP TABLE IF EXISTS `department_account`;
CREATE TABLE `department_account` (
  `id` bigint(20) NOT NULL,
  `accountId` bigint(20) NOT NULL,
  `enterpriseId` bigint(20) NOT NULL,
  `deptId` bigint(20) NOT NULL,
  `cloudUserId` bigint(20) NOT NULL,
  `status` tinyint(1) DEFAULT 0 COMMENT '0 enabled ,1 disabled',
  `accessKeyId` varchar(128)DEFAULT NULL,
  `secretKey` varchar(128) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  `resourceType` int(10) DEFAULT NULL,
  `regionId` int(11) DEFAULT '-1',
  `maxVersions` int(11) DEFAULT '-1',
  `spaceQuota` bigint(20) DEFAULT '-1',
  `teamSpaceFlag` tinyint(4) DEFAULT '1',
  `teamSpaceQuota` bigint(20) DEFAULT '-1',
  `teamSpaceMaxNum` int(11) DEFAULT '-1',
  `uploadBandWidth` bigint(20) DEFAULT '-2',
  `downloadBandWidth` bigint(20) DEFAULT '-2',
  `fileNeedApprove` tinyint(20) DEFAULT '0',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `department_account_UNIQUE_1` (`accountId`, `enterpriseId`, `deptId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `department_account`  ADD UNIQUE INDEX ` enterpriseId_deptId` (`enterpriseId`, `deptId`) USING BTREE ;
/* 组织架构相关 end */

/*Table structure for table `wx_department` */
/*
DROP TABLE IF EXISTS `wx_department`;
CREATE TABLE `wx_department` (
  `id` int(20) unsigned NOT NULL,
  `corpId` varchar(32) NOT NULL,
  `parentid` int(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `order` int(11) NOT NULL,
  `state` tinyint(1) NOT NULL DEFAULT '0',
  `boxEnterpriseId` bigint(20) DEFAULT NULL,
  `boxDepartmentId` bigint(20) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`corpId`,`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
*/

/*Table structure for table `wx_enterprise` */
DROP TABLE IF EXISTS `wx_enterprise`;
CREATE TABLE `wx_enterprise` (
  `id` varchar(32) NOT NULL COMMENT '授权方企业微信id',
  `name` varchar(64) DEFAULT NULL COMMENT '授权方企业微信名称',
  `type` varchar(16) DEFAULT NULL COMMENT '授权方企业微信类型，认证号：verified, 注册号：unverified',
  `squareLogoUrl` varchar(255) DEFAULT NULL COMMENT '授权方企业微信方形头像',
  `userMax` int(11) DEFAULT NULL COMMENT '授权方企业微信用户规模',
  `fullName` varchar(128) DEFAULT NULL COMMENT '所绑定的企业微信主体名称',
  `subjectType` tinyint(1) DEFAULT '1' COMMENT '企业类型，1. 企业; 2. 政府以及事业单位; 3. 其他组织, 4.团队号',
  `verifiedEndTime` datetime DEFAULT NULL COMMENT '认证到期时间',
  `wxqrCode` varchar(256) DEFAULT '2' COMMENT '授权方企业微信二维码',
  `email` varchar(255) DEFAULT NULL COMMENT '授权管理员的邮箱，可能为空（外部管理员一定有，不可更改）',
  `mobile` varchar(32) DEFAULT NULL COMMENT '授权管理员的手机号，可能为空（内部管理员一定有，可更改）',
  `userId` varchar(32) DEFAULT NULL COMMENT '授权管理员的userid，可能为空（内部管理员一定有，不可更改）',
  `permanentCode` varchar(512) DEFAULT NULL COMMENT '企业微信永久授权码,最长为512字节',
  `boxEnterpriseId` bigint(20) DEFAULT NULL COMMENT 'storbox系统enterpriseId',
  `state` tinyint DEFAULT 0 COMMENT '状态',
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `wx_enterprise_user` */
/*
DROP TABLE IF EXISTS `wx_enterprise_user`;
CREATE TABLE `wx_enterprise_user` (
  `corpId` varchar(32) NOT NULL,
  `userId` varchar(32) NOT NULL,
  `departmentId` int(20) NOT NULL DEFAULT '0',
  `name` varchar(32) NOT NULL,
  `order` int(11) DEFAULT NULL,
  `position` varchar(32) DEFAULT NULL,
  `mobile` varchar(16) DEFAULT NULL,
  `gender` int(11) DEFAULT NULL,
  `email` varchar(32) DEFAULT NULL,
  `isLeader` int(11) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `telephone` varchar(16) DEFAULT NULL,
  `englishname` varchar(32) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `boxEnterpriseId` bigint(20) DEFAULT NULL,
  `boxEnterpriseUserId` bigint(20) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`corpId`,`userId`),
  KEY `wx_enterprise_user_index_enterpriseId_departmentId` (`corpId`,`departmentId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
*/
-- ----------------------------
-- 企业内部权限控制表
-- ----------------------------
DROP TABLE IF EXISTS `enterprise_security_privilege`;
CREATE TABLE `enterprise_security_privilege` (
  `id` bigint(20) NOT NULL,
  `enterpriseId` bigint(20) NOT NULL,
  `departmentId` bigint(20) NOT NULL DEFAULT '0',
  `role` tinyint(4) NOT NULL,
  `enterpriseUserId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `enterprise_security_privilege_index` (`enterpriseId`,`departmentId`,`role`,`enterpriseUserId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- IM帐号表
-- ----------------------------
DROP TABLE IF EXISTS `im_account`;
CREATE TABLE `im_account` (
  `IMaccid` varchar(255) NOT NULL,
  `IMPwd` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `icon` varchar(255) DEFAULT NULL,
  `enterpriseId` bigint(20) DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  `accountId` bigint(20) DEFAULT NULL,
  `cloudUserId` bigint(20) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  PRIMARY KEY (`IMaccid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 微信用户信息
-- ----------------------------
DROP TABLE IF EXISTS `wx_user`;
CREATE TABLE `wx_user` (
  `unionId` varchar(128) NOT NULL COMMENT '用户在微信开放平台的唯一标识符',
  `openId` varchar(128) DEFAULT NULL COMMENT '用户唯一标识',
  `nickName` varchar(64) DEFAULT NULL,
  `gender` tinyint(4) DEFAULT NULL,
  `mobile` varchar(16) DEFAULT NULL,
  `email` varchar(32) DEFAULT NULL,
  `country` varchar(32) DEFAULT NULL,
  `province` varchar(32) DEFAULT NULL,
  `city` varchar(32) DEFAULT NULL,
  `language` varchar(32) DEFAULT NULL,
  `avatarUrl` varchar(255) DEFAULT NULL,
  `uin` varchar(128) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  `status` TINYINT DEFAULT 0,
  `cloudUserId` bigint(20) DEFAULT 0,
  `regionId` int(11) DEFAULT 0,
  `quota` bigint(20) DEFAULT 0,
  `type` TINYINT DEFAULT 0,
  PRIMARY KEY (`unionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 微信用户-企业信息
-- ----------------------------
DROP TABLE IF EXISTS `wx_user_enterprise`;
CREATE TABLE `wx_user_enterprise` (
  `unionId` varchar(32) NOT NULL,
  `enterpriseId` bigint(20) NOT NULL,
  `enterpriseUserId` bigint(20) NOT NULL,
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`unionId`,`enterpriseId`),
  KEY `index_unionId_enterpriseId_userId` (`unionId`,`enterpriseId`, `enterpriseUserId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `secret_staff`;
CREATE TABLE `secret_staff` (
  `secretLevel` tinyint(2) DEFAULT NULL,
  `staffLevel` tinyint(2) DEFAULT NULL,
  `accountId` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for `wx_robot`
-- ----------------------------
DROP TABLE IF EXISTS `wx_robot`;
CREATE TABLE `wx_robot` (
  `id` bigint(20) NOT NULL,
  `accountId` bigint(20) NOT NULL,
  `cloudUserId` bigint(20) NOT NULL,
  `wxUin` varchar(20) NOT NULL,
  `wxName` varchar(100) DEFAULT NULL,
  `status` tinyint(2) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `lastStartAt` datetime DEFAULT NULL,
   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for `wx_robot_config`
-- ----------------------------
DROP TABLE IF EXISTS `wx_robot_config`;
CREATE TABLE `wx_robot_config` (
  `robotId` bigint(20) NOT NULL,
  `name` varchar(100) NOT NULL,
  `value` bigint(20) NOT NULL,
  `type` tinyint(2) NOT NULL,
  PRIMARY KEY (`robotId`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `wxwork_corp_app`;
CREATE TABLE `wxwork_corp_app` (
  `corpId` varchar(32) NOT NULL COMMENT '企业id',
  `agentId` int(11) NOT NULL COMMENT '授权方应用id',
  `suiteId` varchar(32) DEFAULT NULL COMMENT '应用套件id',
  `appId` int(11) DEFAULT NULL COMMENT '服务商套件中的对应应用id',
  `name` varchar(64) DEFAULT NULL,
  `roundLogoUrl` varchar(128) DEFAULT NULL,
  `squareLogoUrl` varchar(128) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`corpId`,`agentId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `message_template`;
CREATE TABLE `message_template` (
  `id` VARCHAR(64) NOT NULL,
  `content` VARCHAR(1024) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

insert  into `message_template`(`id`,`content`) values ('createAdmin','您好，您的管理员账户已经生成。<br>用户名: ${username}<br> 密码: ${password}<br>请尽快登录<a href=\"http://www.jmapi.cn/ecm\">www.jmapi.cn/ecm</a>修改。');
insert  into `message_template`(`id`,`content`) values ('createUser','您好，你的微信文件宝已经开通：<br>你的账号如下：<br>企业名称：${enterpriseName}<br>用户名：${username}<br> 密码: ${password}<br>您可以在微信小程序中搜索“企业云盘”，进入小程序进行绑定。绑定后你可以访问<a href=\"http://www.jmapi.cn\">www.jmapi.cn</a>。通过微信扫描登陆管理您的知识文档，与同事进行工作协同。您也可以登陆<a href=\"http://www.jmapi.cn\">www.jmapi.cn</a>。首次登陆通过企业微信扫描登陆，然后在设置->用户信息中绑定您的微信号码。');
insert  into `message_template`(`id`,`content`) values ('resetPassword','您好，您的账号密码已经被重置。<br>用户名：${username}<br>新密码: ${password}<br>请尽快登录<a href=\"http://www.jmapi.cn\">www.jmapi.cn</a>修改密码。');

<<<<<<< HEAD
-- ----------------------------
-- Table structure for `product`
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `type` tinyint(4) NOT NULL,
  `accountNum` bigint(20) NOT NULL,
  `accountSpace` bigint(20) NOT NULL,
  `teamNum` bigint(20) NOT NULL,
  `teamSpace` bigint(20) NOT NULL,
  `introduce` varchar(255) NOT NULL,
  `companyName` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4;


-- ----------------------------
-- Table structure for `product_rebate`
-- ----------------------------
DROP TABLE IF EXISTS `product_rebate`;
CREATE TABLE `product_rebate` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `productId` bigint(20) NOT NULL,
  `duration` bigint(20) DEFAULT NULL,
  `DiscountRatio` float(6,2) NOT NULL,
  `startDate` date DEFAULT NULL,
  `endDate` date DEFAULT NULL,
  `introduce` varchar(255) DEFAULT NULL,
  `createDate` datetime DEFAULT NULL,
  `updateDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4;
=======
>>>>>>> refs/remotes/origin/master

-- ----------------------------
-- Table structure for `order_bill`
-- ----------------------------
DROP TABLE IF EXISTS `order_bill`;
CREATE TABLE `order_bill` (
  `id` varchar(32) NOT NULL,
  `productId` bigint(20) NOT NULL,
  `duration` tinyint(9) NOT NULL,
  `totalPrice` bigint(20) DEFAULT NULL,
  `discountRatio` float(6,2) DEFAULT NULL,
  `discountPrice` bigint(20) DEFAULT NULL,
  `surplusCost` bigint(20) DEFAULT NULL,
  `payMoney` bigint(20) DEFAULT NULL,
  `type` tinyint(4) NOT NULL,
  `userType` tinyint(4) NOT NULL,
  `userType` tinyint(4) NOT NULL,
  `submitDate` datetime NOT NULL,
  `finishedDate` datetime DEFAULT NULL,
  `enterpriseId` bigint(20) NOT NULL,
  `enterpriseUserId` bigint(20) NOT NULL,
  `accountId` bigint(20) DEFAULT NULL,
  `cloudUserId` bigint(20) DEFAULT NULL,
  `descript` varchar(255) DEFAULT NULL,
  `status` tinyint(4) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for `user_vip`
-- ----------------------------
DROP TABLE IF EXISTS `user_vip`;
CREATE TABLE `user_vip` (
  `enterpriseId` bigint(20) NOT NULL,
  `enterpriseUserId` bigint(20) NOT NULL,
  `enterpriseAccountId` bigint(20) NOT NULL,
  `cloudUserId` bigint(20) NOT NULL,
  `productId` bigint(20) NOT NULL,
  `startDate` date NOT NULL,
  `expireDate` date NOT NULL,
  `updateDate` datetime DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for `enterprise_vip`
-- ----------------------------
DROP TABLE IF EXISTS `enterprise_vip`;
CREATE TABLE `enterprise_vip` (
  `enterpriseAccountId` bigint(20) DEFAULT NULL,
  `enterpriseId` bigint(20) DEFAULT NULL,
  `productId` bigint(20) DEFAULT NULL,
  `startDate` date DEFAULT NULL,
  `expireDate` date DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `payment_info`;
CREATE TABLE `payment_info` (
  `id` bigint(20) NOT NULL,
  `orderId` bigint(20) NOT NULL,
  `payAccount` varchar(255) NOT NULL,
  `payAmout` double NOT NULL,
  `payDate` datetime NOT NULL,
  `remark` varchar(255) NOT NULL,
  `type` tinyint(4) NOT NULL,
  PRIMARY KEY (`orderId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `user_department`;
CREATE TABLE `user_department` (
  `enterpriseId` bigint(20) NOT NULL,
  `enterpriseUserId` bigint(20) NOT NULL,
  `departmentId` bigint(20) NOT NULL,
  `order` int NOT NULL DEFAULT 0,
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`enterpriseId`,`enterpriseUserId`,`departmentId`),
  UNIQUE KEY `idx_enterprise_dept_user` (`enterpriseId`,`departmentId`,`enterpriseUserId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for `product`
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `type` tinyint(4) NOT NULL,
  `accountNum` bigint(20) NOT NULL,
  `accountSpace` bigint(20) NOT NULL,
  `teamNum` bigint(20) NOT NULL,
  `teamSpace` bigint(20) NOT NULL,
  `introduce` varchar(255) NOT NULL,
  `companyName` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES ('1', '企业高级版', '2', '40', '2147483648', '40', '10737418240', '企业高级版', '华一云网');
INSERT INTO `product` VALUES ('2', '企业专业版', '2', '80', '10737418240', '80', '53687091200', '企业专业版', '华一云网');
INSERT INTO `product` VALUES ('3', '企业旗舰版', '2', '200', '53687091200', '200', '107374182400', '企业旗舰版', '华一云网');
INSERT INTO `product` VALUES ('4', '黄金VIP', '1', '1', '5368709120', '0', '0', '扩展容量5G', '华一云网');
INSERT INTO `product` VALUES ('5', '黄金VIP', '1', '1', '10737418240', '0', '0', '扩展容量10G', '华一云网');
INSERT INTO `product` VALUES ('6', '铂金VIP', '1', '1', '21474836480', '0', '0', '扩展容量20G', '华一云网');
INSERT INTO `product` VALUES ('7', '铂金VIP', '1', '1', '51539607552', '0', '0', '扩展容量48G', '华一云网');
INSERT INTO `product` VALUES ('8', '钻石VIP', '1', '1', '94489280512', '0', '0', '扩展容量88G', '华一云网');

-- ----------------------------
DROP TABLE IF EXISTS `product_duration_price`;
CREATE TABLE `product_duration_price` (
  `id` tinyint(4) NOT NULL,
  `productId` bigint(20) DEFAULT NULL,
  `duration` tinyint(4) DEFAULT NULL,
  `price` float(20,0) DEFAULT NULL,
  `createDate` datetime DEFAULT NULL,
  `updateDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



-- Records of product_duration_price
-- ----------------------------
INSERT INTO `product_duration_price` VALUES ('1', '4', '3', '650', '2018-03-20 16:01:04', '2018-03-20 16:01:07');
INSERT INTO `product_duration_price` VALUES ('2', '4', '12', '2000', '2018-03-20 16:01:56', '2018-03-20 16:01:59');
INSERT INTO `product_duration_price` VALUES ('3', '4', '24', '4000', '2018-03-20 16:02:30', '2018-03-20 16:02:33');
INSERT INTO `product_duration_price` VALUES ('4', '4', '36', '6000', '2018-03-20 16:03:03', '2018-03-20 16:03:06');
INSERT INTO `product_duration_price` VALUES ('5', '5', '12', '3800', '2018-03-20 16:03:55', '2018-03-20 16:03:58');
INSERT INTO `product_duration_price` VALUES ('6', '5', '24', '7600', '2018-03-20 16:04:23', '2018-03-20 16:04:26');
INSERT INTO `product_duration_price` VALUES ('7', '5', '36', '11400', '2018-03-20 16:04:38', '2018-03-20 16:04:40');
INSERT INTO `product_duration_price` VALUES ('8', '6', '12', '5800', '2018-03-20 16:05:09', '2018-03-20 16:05:11');
INSERT INTO `product_duration_price` VALUES ('9', '6', '24', '11600', '2018-03-20 16:05:28', '2018-03-20 16:05:30');
INSERT INTO `product_duration_price` VALUES ('10', '6', '36', '17400', '2018-03-20 16:08:56', '2018-03-20 16:08:58');
INSERT INTO `product_duration_price` VALUES ('11', '7', '12', '9900', '2018-03-20 16:09:45', '2018-03-20 16:09:47');
INSERT INTO `product_duration_price` VALUES ('12', '7', '24', '19800', '2018-03-20 16:10:08', '2018-03-20 16:10:10');
INSERT INTO `product_duration_price` VALUES ('13', '7', '36', '29800', '2018-03-20 16:10:30', '2018-03-20 16:10:33');
INSERT INTO `product_duration_price` VALUES ('14', '8', '12', '16800', '2018-03-20 16:11:01', '2018-03-20 16:11:04');
INSERT INTO `product_duration_price` VALUES ('15', '8', '24', '33600', '2018-03-20 16:11:24', '2018-03-20 16:11:27');
INSERT INTO `product_duration_price` VALUES ('16', '8', '36', '50400', '2018-03-20 16:11:48', '2018-03-20 16:11:51');



-- ----------------------------
-- Table structure for `product_rebate`
-- ----------------------------
DROP TABLE IF EXISTS `product_rebate`;
CREATE TABLE `product_rebate` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `productId` bigint(20) NOT NULL,
  `duration` bigint(20) DEFAULT NULL,
  `DiscountRatio` float(6,2) NOT NULL,
  `startDate` date DEFAULT NULL,
  `endDate` date DEFAULT NULL,
  `introduce` varchar(255) DEFAULT NULL,
  `createDate` datetime DEFAULT NULL,
  `updateDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of product_rebate
-- ----------------------------
INSERT INTO `product_rebate` VALUES ('1', '4', '24', '0.90', '2018-01-13', '2018-01-13', '123', '2018-01-16 00:00:00', '2018-01-18 00:00:00');
INSERT INTO `product_rebate` VALUES ('2', '4', '36', '0.80', '2018-01-13', '2018-01-13', '234', '2018-01-13 16:55:11', '2018-01-13 16:55:14');
INSERT INTO `product_rebate` VALUES ('3', '5', '24', '0.90', '2018-01-13', '2018-01-13', '235', '2018-01-13 16:55:44', '2018-01-13 16:55:46');
INSERT INTO `product_rebate` VALUES ('4', '5', '36', '0.80', '2018-01-13', '2018-01-13', '123', '2018-01-13 16:56:23', '2018-01-13 16:56:26');
INSERT INTO `product_rebate` VALUES ('5', '6', '24', '0.90', '2018-01-13', '2018-01-13', '235', '2018-01-13 16:56:59', '2018-01-13 16:57:01');
INSERT INTO `product_rebate` VALUES ('6', '6', '36', '0.80', '2018-01-13', '2018-01-13', '235', '2018-01-13 16:57:34', '2018-01-13 16:57:37');
INSERT INTO `product_rebate` VALUES ('13', '7', '24', '0.90', '2018-03-20', '2018-03-20', '33', '2018-03-20 16:24:22', '2018-03-20 16:24:24');
INSERT INTO `product_rebate` VALUES ('14', '7', '36', '0.80', '2018-03-20', '2018-03-20', '55', '2018-03-20 16:24:55', '2018-03-20 16:24:57');
INSERT INTO `product_rebate` VALUES ('15', '8', '24', '0.90', '2018-03-20', '2018-03-20', '55', '2018-03-20 16:25:26', '2018-03-20 16:25:29');
INSERT INTO `product_rebate` VALUES ('16', '8', '36', '0.80', '2018-03-20', '2018-03-20', '55', '2018-03-20 16:25:50', '2018-03-20 16:25:52');
INSERT INTO `product_rebate` VALUES ('20', '1', '12', '0.90', '2018-01-17', '2018-01-17', '11', '2018-01-17 19:39:40', '2018-01-17 19:39:42');
INSERT INTO `product_rebate` VALUES ('21', '1', '24', '0.80', '2018-01-17', '2018-01-17', '11', '2018-01-17 19:40:05', '2018-01-17 19:40:08');
INSERT INTO `product_rebate` VALUES ('22', '2', '12', '0.90', '2018-01-17', '2018-01-17', '11', '2018-01-17 19:40:33', '2018-01-17 19:40:35');
INSERT INTO `product_rebate` VALUES ('23', '2', '24', '0.80', '2018-01-17', '2018-01-17', '11', '2018-01-17 19:41:20', '2018-01-17 19:41:22');
INSERT INTO `product_rebate` VALUES ('24', '3', '12', '0.90', '2018-01-17', '2018-01-17', '22', '2018-01-17 19:41:44', '2018-01-17 19:41:47');
INSERT INTO `product_rebate` VALUES ('25', '3', '24', '0.80', '2018-01-17', '2018-01-17', '11', '2018-01-17 19:42:22', '2018-01-17 19:42:24');


DROP TABLE IF EXISTS `share_level`;
CREATE TABLE `share_level` (
  `id` int(20) NOT NULL,
  `startRange` bigint(20) DEFAULT NULL,
  `endRange` bigint(20) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `iconUrl` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `proportions` float(20,4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of share_level
-- ----------------------------
INSERT INTO `share_level` VALUES ('1', '0', '10', '青铜会员 ', null, '青铜会员', '0.0300');
INSERT INTO `share_level` VALUES ('2', '10', '30', '白银会员', null, '白银会员', '0.0400');
INSERT INTO `share_level` VALUES ('3', '30', '100', '黄金会员', null, '黄金会员', '0.0500');
INSERT INTO `share_level` VALUES ('4', '100', '500', '铂金会员', null, '铂金会员', '0.0600');
INSERT INTO `share_level` VALUES ('5', '500', '999999999', '王者会员', null, '王者会员', '0.0800');




-- ----------------------------
-- Table structure for `user_profit_detail`
-- ----------------------------
DROP TABLE IF EXISTS `user_profit_detail`;
CREATE TABLE `user_profit_detail` (
  `id` varchar(32) NOT NULL,
  `userType` tinyint(2) NOT NULL,
  `cloudUserId` bigint(20) NOT NULL,
  `unionID` varchar(255) DEFAULT NULL,
  `openId` varchar(255) DEFAULT NULL,
  `orderId` varchar(255) NOT NULL,
  `enterpriseId` bigint(20) DEFAULT NULL,
  `userName` varchar(255) DEFAULT NULL,
  `payMoney` decimal(20,2) NOT NULL,
  `createAt` datetime DEFAULT NULL,
  `finishAt` datetime DEFAULT NULL,
  `status` tinyint(2) DEFAULT NULL,
  `type` tinyint(2) DEFAULT NULL,
  `proportions` double(20,4) DEFAULT NULL,
  `attempts` tinyint(20) DEFAULT NULL,
  `failReason` varchar(255) DEFAULT NULL,
  `source` varchar(255) DEFAULT NULL,
  `siteId` varchar(255) DEFAULT NULL,
  `appId` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `orderId` (`orderId`) USING BTREE,
  KEY `type_status` (`status`,`type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;





