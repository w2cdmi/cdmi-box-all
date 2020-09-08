/*
SQLyog 企业版 - MySQL GUI v8.14 
MySQL - 5.7.19 : Database - logagent
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`logagent` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `logagent`;

/*Table structure for table `db_version` */

DROP TABLE IF EXISTS `db_version`;

CREATE TABLE `db_version` (
  `version` varchar(128) NOT NULL,
  `modified` date NOT NULL,
  `description` varchar(2048) NOT NULL,
  `sequence` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`version`),
  KEY `sequence` (`sequence`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

/*Data for the table `db_version` */

insert  into `db_version`(`version`,`modified`,`description`,`sequence`) values ('1.3.00.3202','2017-12-14','',2);

/*Table structure for table `logagent` */

DROP TABLE IF EXISTS `logagent`;

CREATE TABLE `logagent` (
  `id` int(11) NOT NULL,
  `clusterId` int(11) NOT NULL,
  `protocol` varchar(64) NOT NULL,
  `address` varchar(1024) NOT NULL,
  `port` int(10) NOT NULL,
  `contextpath` varchar(128) DEFAULT NULL,
  `createdAt` datetime NOT NULL,
  `modifiedAt` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_idx_clusterId` (`clusterId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `logagent` */

/*Table structure for table `logagent_config` */

DROP TABLE IF EXISTS `logagent_config`;

CREATE TABLE `logagent_config` (
  `key` varchar(128) NOT NULL,
  `value` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `logagent_config` */

/*Table structure for table `logagent_fs_endpoint` */

DROP TABLE IF EXISTS `logagent_fs_endpoint`;

CREATE TABLE `logagent_fs_endpoint` (
  `id` int(11) NOT NULL,
  `fsType` varchar(256) NOT NULL,
  `endpoint` varchar(4096) NOT NULL,
  `isCurrent` int(11) NOT NULL,
  `createdAt` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `logagent_fs_endpoint` */

/*Table structure for table `logagent_node` */

DROP TABLE IF EXISTS `logagent_node`;

CREATE TABLE `logagent_node` (
  `id` varchar(64) NOT NULL,
  `name` varchar(128) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `logagent_node` */

/*Table structure for table `logfile_object` */

DROP TABLE IF EXISTS `logfile_object`;

CREATE TABLE `logfile_object` (
  `id` varchar(64) NOT NULL,
  `fileName` varchar(128) NOT NULL,
  `logType` varchar(32) NOT NULL,
  `archiveTime` datetime DEFAULT NULL,
  `nodeId` varchar(64) DEFAULT NULL,
  `nodeName` varchar(128) NOT NULL,
  `size` bigint(20) DEFAULT '0',
  `fsType` varchar(256) NOT NULL,
  `path` varchar(4096) NOT NULL,
  `saveName` varchar(256) NOT NULL,
  `endpointId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_filename` (`fileName`),
  KEY `idx_nodeId` (`nodeId`),
  KEY `idx_nodeName` (`nodeName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `logfile_object` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
