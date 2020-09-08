CREATE DATABASE  IF NOT EXISTS `monitordb`;
USE `monitordb`;

drop table if exists tbl_job_scheduler_lock;
CREATE TABLE `tbl_job_scheduler_lock` (                            
    `ID` int(11) NOT NULL,                     
    `TIME` DATETIME NOT NULL,                            
     PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;
insert into tbl_job_scheduler_lock values (1,now());

DROP TABLE IF EXISTS `service_node`;
CREATE TABLE `service_node` (
  `regionId` int(11) DEFAULT NULL,
  `dcId` int(11) DEFAULT NULL,
  `clusterId` int(11) NOT NULL,
  `clusterType` varchar(64) NOT NULL,
  `clusterName` varchar(127) DEFAULT NULL,
  `name` varchar(128) NOT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


