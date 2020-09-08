use uam;
-- ----------------------------
-- ----------From V1R3C10 to V1R5C00RC01-------------------
ALTER TABLE user_locked ADD INDEX(appId, userId);
ALTER TABLE enterprise_account ADD `createdAt` datetime DEFAULT NULL;
ALTER TABLE enterprise_account ADD `modifiedAt` datetime DEFAULT NULL;
ALTER TABLE enterprise_account ADD `maxSpace` bigint(20) DEFAULT '999999999999';
ALTER TABLE enterprise_account ADD `maxMember` int(11) DEFAULT '99999999';
ALTER TABLE enterprise_account ADD `maxFiles` bigint(20) DEFAULT '99999999';
ALTER TABLE enterprise_account ADD `maxTeamspace` int(11) DEFAULT '99999999';
ALTER TABLE enterprise_account ADD `filePreviewable` tinyint(1) DEFAULT '0';
ALTER TABLE enterprise_account ADD `fileScanable` tinyint(1) DEFAULT '0';
ALTER TABLE enterprise_account modify column `secretKey` varchar(2048);
ALTER TABLE enterprise_account ADD `secretKeyEncodeKey` varchar(2048) NOT NULL;
ALTER TABLE system_config modify column `value` varchar(2048);
ALTER TABLE `user` modify column `validateKey` varchar(2048);

ALTER TABLE enterprise ADD `description` varchar(255) DEFAULT NULL;


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
  `webLogo` MEDIUMBLOB,
  `pcLogo` MEDIUMBLOB,
  `icon` MEDIUMBLOB,
  `webLogoFormatName` varchar(32) DEFAULT NULL,
  `pcLogoFormatName` varchar(32) DEFAULT NULL,
  `iconFormatName` varchar(32) DEFAULT NULL,
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

ALTER TABLE `mailserver` ADD `authPasswordEncodeKey` varchar(2048) DEFAULT NULL;
ALTER TABLE `mailserver` modify column `authPassword` varchar(2048);

ALTER TABLE `admin` modify column `validateKey` varchar(2048);
ALTER TABLE `admin` modify column `dynamicPassword` varchar(2048);

ALTER TABLE `authapp_accesskey` modify column `secretKey` varchar(2048);
ALTER TABLE `authapp_accesskey` ADD `secretKeyEncodeKey` varchar(2048) NOT NULL;

ALTER TABLE `authapp` modify column `ufmSecretKey` varchar(2048);
ALTER TABLE `authapp` ADD `ufmSecretKeyEncodeKey` varchar(2048) NOT NULL;

ALTER TABLE client_manage ADD `arithmetic` varchar(64) DEFAULT NULL;

alter table enterprise modify column  contactPhone varchar(255);

ALTER TABLE admin ADD `lastLoginIP` varchar(255) DEFAULT NULL;

DROP PROCEDURE IF EXISTS alter_logininfo_sub_table_pi2;
DELIMITER $$
CREATE PROCEDURE alter_logininfo_sub_table_pi2()
SQL SECURITY INVOKER
BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 0;
    WHILE i<100 DO
        SET aa=CONCAT('logininfo_',i);
        SET @alertobjfiles= CONCAT('ALTER TABLE ',aa,' ADD `domainName` varchar(64) NOT NULL;');
        SET @alertobjfiles2= CONCAT('ALTER TABLE ',aa,' DROP INDEX logininfo_UNIQUE;');
        PREPARE stmt FROM @alertobjfiles;
        PREPARE stmt2 FROM @alertobjfiles2;
        EXECUTE stmt;
        EXECUTE stmt2;
        SET i=i+1;
    END WHILE;
END $$
DELIMITER ;
CALL  alter_logininfo_sub_table_pi2;

DROP PROCEDURE IF EXISTS alter_logininfo_sub_table_pi2_2;
DELIMITER $$
CREATE PROCEDURE alter_logininfo_sub_table_pi2_2()
SQL SECURITY INVOKER
BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 0;
    WHILE i<100 DO
        SET aa=CONCAT('logininfo_',i);
        SET @alertobjfiles3= CONCAT('update ',aa,' set domainName=(select domainName from enterprise where id=enterpriseId);');
        SET @alertobjfiles5= CONCAT('alter table ',aa,' add unique key `logininfo_UNIQUE`(`loginName`,`domainName`);');
        PREPARE stmt3 FROM @alertobjfiles3;
		PREPARE stmt5 FROM @alertobjfiles5;
        EXECUTE stmt3;
		EXECUTE stmt5;
        SET i=i+1;
    END WHILE;
END $$
DELIMITER ;
CALL  alter_logininfo_sub_table_pi2_2;

INSERT INTO system_config VALUES ('lock.time', '5', -1);
INSERT INTO system_config VALUES ('lock.failtimes', '5', -1);
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ( 'UserLoginLogCreateTableJob','uam','UserLoginLogCreateTableJob',1,'userLoginLogCreateTablesTask','5',1,'0 0 1 * * ?','100','1',-1,-1,'1','');
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ( 'UserLoginLogDropTableJob','uam','UserLoginLogDropTableJob',1,'userLoginLogDropTablesTask','5',1,'0 0 6 * * ?','100','1',-1,-1,'1','');
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) 
	values ('UserLoginLogCreateTableJob','uam','0','0',1);
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) 
	values ('UserLoginLogDropTableJob','uam','0','0',1);
	
delete from system_job_def where name='clearRedundantJobExecuteRecordJobFor';
delete from system_job_runtimeinfo where jobName='clearRedundantJobExecuteRecordJobFor';

insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('bmsNetworkCheckJob','bms','bmsNetworkCheckJob',1,'bmsNetworkCheckJob','5',0,'0/15 * * * * ?','100','1',-1,-1,'1','');
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('cloudappNetworkCheckJob','cloudapp','cloudappNetworkCheckJob',1,'cloudappNetworkCheckJob','5',0,'0/15 * * * * ?','100','1',-1,-1,'1','');
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('uamNetworkCheckJob','uam','uamNetworkCheckJob',1,'uamNetworkCheckJob','5',0,'0/15 * * * * ?','100','1',-1,-1,'1','');
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('clearRedundantJobExecuteRecordJob','uam','clearRedundantJobExecuteRecordJobForUAM',1,'clearRedundantJobExecuteRecordJob','',1,'15 1 0/1 * * ?','100','1',-1,-1,'1','');
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('clearRedundantJobExecuteRecordJob','bms','clearRedundantJobExecuteRecordJobForBMS',1,'clearRedundantJobExecuteRecordJob','',1,'15 1 0/1 * * ?','100','1',-1,-1,'1','');
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) values ('clearRedundantJobExecuteRecordJob','cloudapp','clearRedundantJobExecuteRecordJobForCloudapp',1,'clearRedundantJobExecuteRecordJob','',1,'15 1 0/1 * * ?','100','1',-1,-1,'1','');
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('bmsNetworkCheckJob','bms','0','0',1);
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('cloudappNetworkCheckJob','cloudapp','0','0',1);
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('uamNetworkCheckJob','uam','0','0',1);
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('clearRedundantJobExecuteRecordJob','uam','0','0',1);
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('clearRedundantJobExecuteRecordJob','bms','0','0',1);
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('clearRedundantJobExecuteRecordJob','cloudapp','0','0',1);

DROP TABLE IF EXISTS `multi_system_config`;
CREATE TABLE `multi_system_config` (
  `accountId` int(20) NOT NULL,
  `userSpaceQuota` varchar(255) NOT NULL,
  `userVersions` varchar(255) NOT NULL,
  `enableTeamSpace` varchar(255) NOT NULL,
  `maxTeamSpaces` varchar(255) DEFAULT '0',
  `teamSpaceQuota` varchar(255) DEFAULT '0',
  `teamSpaceVersions` varchar(255) DEFAULT '0',
  PRIMARY KEY (`accountId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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

ALTER TABLE mailserver ADD `mailSecurity` varchar(255) DEFAULT 'tls' NOT NULL;
update mailserver set mailSecurity='false' where enableSsl=0;
update mailserver set mailSecurity='ssl' where enableSsl!=0;
ALTER TABLE mailserver drop column `enableSsl`;

DROP PROCEDURE IF EXISTS alter_enterprise_user_key;
DELIMITER $$
CREATE PROCEDURE alter_enterprise_user_key()
SQL SECURITY INVOKER
BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 0;
    WHILE i<100 DO  
        SET aa=CONCAT('enterprise_user_',i);
        SET @alertobjfiles= CONCAT('ALTER TABLE ',aa,' modify column `password` varchar(1024) DEFAULT NULL;');
        SET @alertobjfiles1= CONCAT('ALTER TABLE ',aa,' modify column `validateKey` varchar(1024) DEFAULT NULL;');
        SET @alertobjfiles2= CONCAT('ALTER TABLE ',aa,' ADD `validateKeyEncodeKey` varchar(1024) DEFAULT NULL;');
        PREPARE stmt FROM @alertobjfiles;
        PREPARE stmt1 FROM @alertobjfiles1;
        PREPARE stmt2 FROM @alertobjfiles2;
        EXECUTE stmt;
        EXECUTE stmt1;
        EXECUTE stmt2;
        SET i=i+1;
    END WHILE;
END $$
DELIMITER ;
CALL  alter_enterprise_user_key;


DROP TABLE IF EXISTS `manager_locked`;
CREATE TABLE `manager_locked` (
  `loginName` varchar(127) NOT NULL,
  `createdAt` datetime DEFAULT NULL,
  `lockedAt` datetime DEFAULT NULL,
  `loginFailTimes` int(11) DEFAULT NULL,
  PRIMARY KEY (`loginName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

alter table account_authserver change authserverId authServerId bigint(20) NOT NULL;

ALTER TABLE `access_config` ADD `spaceRoleId` bigint(20) DEFAULT -1;
ALTER TABLE `access_config` DROP INDEX `access_config_union_index`;
ALTER TABLE `access_config` ADD UNIQUE INDEX `access_config_union_index` (`accountId`,`safeRoleId`,`spaceRoleId`, `netRegionId`,`clientType`) ;