/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50517
Source Host           : localhost:3306
Source Database       : monitordb

Target Server Type    : MYSQL
Target Server Version : 50517
File Encoding         : 65001

Date: 2016-01-07 18:03:13
*/
create database if not exists `monitordb`;

use `monitordb`;
SET FOREIGN_KEY_CHECKS=0;


-- ----------------------------
-- Table structure for `system_cluster_info`
-- ----------------------------
DROP TABLE IF EXISTS `system_cluster_info`;
CREATE TABLE `system_cluster_info` (
  `systemName` varchar(64) NOT NULL COMMENT '界面显示的系统集群名称，如UAS，DSS-1，DSS-2',
  `resourceGrounpId` int(11) NOT NULL DEFAULT -1 COMMENT '资源组ID',
  `type` int(11) NOT NULL DEFAULT 0 COMMENT '0:UAS,1:DSS',
  `clusterName` varchar(64) NOT NULL DEFAULT '' COMMENT '上报集群集群名称',
  `status` int(11) NOT NULL DEFAULT 0 COMMENT '集群状态 0：正常，1：完全故障，2：部分故障',
  PRIMARY KEY (`systemName`),
  KEY `IDX_C_NAME` (`clusterName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;





-- ----------------------------
-- Table structure for `cluster_running_info`
-- ----------------------------
DROP TABLE IF EXISTS `cluster_running_info`;
CREATE TABLE `cluster_running_info` (
  `clusterName` varchar(64) NOT NULL DEFAULT '' COMMENT '集群名称',
  `clusterServiceName` varchar(64) NOT NULL DEFAULT '' COMMENT '集群内服务名称',
  `reportTime` bigint(32) DEFAULT NULL COMMENT '上报时间',
  `type` varchar(64) DEFAULT NULL COMMENT '集群服务类型：MYSQL,ZOOKEEPER,ACTIVEMQ,UFM,DSS,BMS,LVS,Nginx,iSystem',
  `status` int(11) DEFAULT NULL COMMENT '状态 0:表示正常，1：异常',
  `reserve` varchar(512) DEFAULT NULL,
  PRIMARY KEY (`clusterName`,`clusterServiceName`),
  KEY `IDX_C_NAME` (`clusterName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of cluster_running_info
-- ----------------------------

-- ----------------------------
-- Table structure for `cluster_running_info_history`
-- ----------------------------
DROP TABLE IF EXISTS `cluster_running_info_history`;
CREATE TABLE `cluster_running_info_history` (
  `clusterName` varchar(64) NOT NULL DEFAULT '' COMMENT '集群名称',
  `clusterServiceName` varchar(64) NOT NULL DEFAULT '' COMMENT '集群内服务名称',
  `reportTime` bigint(32) NOT NULL COMMENT '上报时间',
  `type` varchar(64) DEFAULT NULL COMMENT '集群服务类型：MYSQL,ZOOKEEPER,ACTIVEMQ,UFM,DSS,BMS,LVS,Nginx,iSystem',
  `status` int(11) DEFAULT NULL COMMENT '状态:0正常，1异常',
  `reserve` varchar(512) DEFAULT NULL,

  PRIMARY KEY (`clusterName`,`clusterServiceName`,`reportTime`),
  KEY `IDX_C_NAME` (`clusterName`),
  KEY `IDX_C_S_NAME` (`clusterServiceName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of cluster_running_info_history
-- ----------------------------
-- ----------------------------
-- Table structure for `cluster_instance_running_info`
-- ----------------------------
DROP TABLE IF EXISTS `cluster_instance_running_info`;
CREATE TABLE `cluster_instance_running_info` (
  `clusterName` varchar(64) NOT NULL COMMENT '集群名称',
  `clusterServiceName` varchar(64) NOT NULL DEFAULT '' COMMENT '集群内服务名称，如MYSQL-1，MYSQL-2',
  `reportTime` bigint(32) DEFAULT NULL COMMENT '上报时间',
  `hostName` varchar(64) NOT NULL DEFAULT '' COMMENT '节点名称或者IP',
  `status` int(11) DEFAULT NULL COMMENT '状态 0:表示正常，1：异常',
  `runRole` varchar(64) DEFAULT NULL COMMENT '运行角色,master,slave,leader,follower',
  `dataStatus` varchar(64) DEFAULT NULL COMMENT '数据库同步状态',
  `vip` varchar(512) DEFAULT NULL,
  `innerIP` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`clusterName`,`clusterServiceName`,`hostName`),
  KEY `IDX_C_NAME` (`clusterName`),
  KEY `IDX_C_S_NAME` (`clusterServiceName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of cluster_instance_running_info
-- ----------------------------

-- ----------------------------
-- Table structure for `cluster_instance_running_info_history`
-- ----------------------------
DROP TABLE IF EXISTS `cluster_instance_running_info_history`;
CREATE TABLE `cluster_instance_running_info_history` (
  `clusterName` varchar(64) NOT NULL COMMENT '集群名称',
  `clusterServiceName` varchar(64) NOT NULL DEFAULT '' COMMENT '集群内服务名称，如MYSQL-1，MYSQL-2',
  `reportTime` bigint(32) NOT NULL COMMENT '上报时间',
  `hostName` varchar(64) NOT NULL DEFAULT '' COMMENT '节点名称或者IP',
  `status` int(11) DEFAULT NULL COMMENT '状态，正常是：normal,异常：abnormal',
  `runRole` varchar(64) DEFAULT NULL COMMENT '运行角色,master,slave,leader,follower',
  `dataStatus` varchar(64) DEFAULT NULL COMMENT '数据库同步状态',
  `vip` varchar(512) DEFAULT NULL,
  `innerIP` varchar(512) DEFAULT NULL,
  PRIMARY KEY (`clusterServiceName`,`reportTime`,`hostName`),
  KEY `IDX_C_NAME` (`clusterName`),
  KEY `IDX_C_S_NAME` (`clusterServiceName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of cluster_instance_running_info_history
-- ----------------------------

DROP TABLE IF EXISTS `node_info`;
CREATE TABLE `node_info` (
  `clusterName` varchar(64) NOT NULL DEFAULT '' COMMENT '集群名',
  `hostName` varchar(64) NOT NULL COMMENT '主机名',
  `type` int(11) NOT NULL DEFAULT 0 COMMENT '0:UAS,1:DSS',
  `managerIp` varchar(64) NOT NULL DEFAULT '' COMMENT '管理ip（节点）',
  `reportTime` bigint(20) NOT NULL COMMENT '上报时间',
  `cpuUsage` double DEFAULT NULL COMMENT 'cpu使用率',
  `cpuCount` int(11) DEFAULT NULL COMMENT 'cpu核心数',
  `cpuThread` int(11) DEFAULT NULL COMMENT 'cpu线程数',
  `memoryUsage` int(11) DEFAULT NULL COMMENT '节点已使用内存',
  `memoryTotal` int(11) DEFAULT NULL COMMENT '节点总内存',
  `memoryRate` double DEFAULT NULL COMMENT '节点内存使用率',
  `serviceIp` varchar(64) DEFAULT NULL COMMENT '业务网类型码',
  `serviceNiccapacity` int(11) DEFAULT NULL COMMENT '业务网容量',
  `serviceRate` double DEFAULT NULL COMMENT '业务网速率',
  `serviceStatus` varchar(64) DEFAULT NULL COMMENT '业务网状态：0（up）、1（down）',
  `manageIp` varchar(64) DEFAULT NULL COMMENT '管理网类型码',
  `manageNiccapacity` int(11) DEFAULT NULL COMMENT '管理网容量',
  `manageRate` double DEFAULT NULL COMMENT '管理网速率',
  `manageStatus` varchar(64) DEFAULT NULL COMMENT '管理网状态：0（up）,1（down）',
  `privateIp` varchar(64) DEFAULT NULL COMMENT '私网类型码',
  `privateNiccapacity` int(11) DEFAULT NULL COMMENT '私网容量',
  `privateRate` double DEFAULT NULL COMMENT '私网速率',
  `privateStatus` varchar(64) DEFAULT NULL COMMENT '私网状态：0（up）、1（down）',
  `ipmi` varchar(64) DEFAULT NULL COMMENT 'ipmi',
  `connectTotal` bigint(32) DEFAULT NULL COMMENT '总连接数',
  `establishedTotal` bigint(32) DEFAULT NULL COMMENT '已建立连接数',
  `fileHandleTotal` int(11) DEFAULT NULL COMMENT '文件句柄数',
  `topInfo` varchar(512) DEFAULT NULL COMMENT 'top命令信息',
  `status` int(11) DEFAULT NULL COMMENT '0:表示正常，1：异常',
  `reserve` varchar(512) DEFAULT NULL COMMENT '保留字段',
  PRIMARY KEY (`clusterName`,`hostName`),
  KEY `IDX_NAME` (`hostName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of node_info
-- ----------------------------

-- ----------------------------
-- Table structure for `node_info_history`
-- ----------------------------
DROP TABLE IF EXISTS `node_info_history`;
CREATE TABLE `node_info_history` (
   `clusterName` varchar(64) NOT NULL DEFAULT '' COMMENT '集群名',
  `hostName` varchar(64) NOT NULL COMMENT '主机名',
  `type` int(11) NOT NULL DEFAULT 0 COMMENT '0:UAS,1:DSS',
  `managerIp` varchar(64) NOT NULL DEFAULT '' COMMENT '管理ip（节点）',
  `reportTime` bigint(20) NOT NULL COMMENT '上报时间',
  `cpuUsage` double DEFAULT NULL COMMENT 'cpu使用率',
  `cpuCount` int(11) DEFAULT NULL COMMENT 'cpu核心数',
  `cpuThread` int(11) DEFAULT NULL COMMENT 'cpu线程数',
  `memoryUsage` int(11) DEFAULT NULL COMMENT '节点已使用内存',
  `memoryTotal` int(11) DEFAULT NULL COMMENT '节点总内存',
  `memoryRate` double DEFAULT NULL COMMENT '节点内存使用率',
  `serviceIp` varchar(64) DEFAULT NULL COMMENT '业务网类型码',
  `serviceNiccapacity` int(11) DEFAULT NULL COMMENT '业务网容量',
  `serviceRate` double DEFAULT NULL COMMENT '业务网速率',
  `serviceStatus` varchar(64) DEFAULT NULL COMMENT '业务网状态：0（up）、1（down）',
  `manageIp` varchar(64) DEFAULT NULL COMMENT '管理网类型码',
  `manageNiccapacity` int(11) DEFAULT NULL COMMENT '管理网容量',
  `manageRate` double DEFAULT NULL COMMENT '管理网速率',
  `manageStatus` varchar(64) DEFAULT NULL COMMENT '管理网状态：0（up）,1（down）',
  `privateIp` varchar(64) DEFAULT NULL COMMENT '私网类型码',
  `privateNiccapacity` int(11) DEFAULT NULL COMMENT '私网容量',
  `privateRate` double DEFAULT NULL COMMENT '私网速率',
  `privateStatus` varchar(64) DEFAULT NULL COMMENT '私网状态：0（up）、1（down）',
  `ipmi` varchar(64) DEFAULT NULL COMMENT 'ipmi',
  `connectTotal` bigint(32) DEFAULT NULL COMMENT '总连接数',
  `establishedTotal` bigint(32) DEFAULT NULL COMMENT '已建立连接数',
  `fileHandleTotal` int(11) DEFAULT NULL COMMENT '文件句柄数',
  `topInfo` varchar(512) DEFAULT NULL COMMENT 'top命令信息',
  `status` int(11) DEFAULT NULL COMMENT '0:表示正常，1：异常',
  `reserve` varchar(512) DEFAULT NULL COMMENT '保留字段',

  PRIMARY KEY (`clusterName`,`hostName`,`reportTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of node_info_history
-- ----------------------------

-- ----------------------------
-- Table structure for `node_disk_info`
-- ----------------------------
DROP TABLE IF EXISTS `node_disk_info`;
CREATE TABLE `node_disk_info` (
  `clusterName` varchar(64) DEFAULT NULL COMMENT '集群名称',
  `hostName` varchar(64) NOT NULL DEFAULT '' COMMENT '管理ip（节点）',
   `reportTime` bigint(20) NOT NULL DEFAULT '0'  COMMENT '上报时间',
  `catalogueName` varchar(64) NOT NULL COMMENT '磁盘目录（/、opt、log、mysql、zk、ramdisk）',
  `total` int(11) DEFAULT NULL COMMENT '总容量',
  `used` int(11) DEFAULT NULL COMMENT '已用容量',
  `residue` int(11) DEFAULT NULL COMMENT '剩余容量',
  `rate` double DEFAULT NULL COMMENT '使用率',
  PRIMARY KEY (`hostName`,`catalogueName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Records of node_disk_info
-- ----------------------------

-- ----------------------------
-- Table structure for `node_disk_info_history`
-- ----------------------------
DROP TABLE IF EXISTS `node_disk_info_history`;
CREATE TABLE `node_disk_info_history` (
  `clusterName` varchar(64) DEFAULT NULL COMMENT '集群名称',
  `hostName` varchar(64) NOT NULL DEFAULT '' COMMENT '管理ip（节点）',
   `reportTime` bigint(20) NOT NULL DEFAULT '0'  COMMENT '上报时间',
  `catalogueName` varchar(64) NOT NULL COMMENT '磁盘目录（/、opt、log、mysql、zk、ramdisk）',
  `total` int(11) DEFAULT NULL COMMENT '总容量',
  `used` int(11) DEFAULT NULL COMMENT '已用容量',
  `residue` int(11) DEFAULT NULL COMMENT '剩余容量',
  `rate` double DEFAULT NULL COMMENT '使用率',
  PRIMARY KEY (`hostName`,`catalogueName`,`reportTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of node_disk_info_history
-- ----------------------------
DROP TABLE IF EXISTS `node_disk_IO`;
CREATE TABLE `node_disk_IO` (
  `clusterName` varchar(64) DEFAULT NULL COMMENT '集群名称',
  `hostName` varchar(64) NOT NULL DEFAULT '' COMMENT '管理（节点）',
   `reportTime` bigint(20) NOT NULL DEFAULT '0'  COMMENT '上报时间',
  `diskName` varchar(64) NOT NULL COMMENT '磁盘名',
  `avgeResponeTime` int(11) DEFAULT NULL COMMENT '平均响应时间',
  `rate` double DEFAULT NULL COMMENT '使用率',
  PRIMARY KEY (`hostName`,`diskName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `node_disk_IO_history`;
CREATE TABLE `node_disk_IO_history` (
  `clusterName` varchar(64) DEFAULT NULL COMMENT '集群名称',
  `hostName` varchar(64) NOT NULL DEFAULT '' COMMENT '管理（节点）',
   `reportTime` bigint(20) NOT NULL DEFAULT '0'  COMMENT '上报时间',
  `diskName` varchar(64) NOT NULL COMMENT '磁盘名',
  `avgeResponeTime` int(11) DEFAULT NULL COMMENT '平均响应时间',
  `rate` double DEFAULT NULL COMMENT '使用率',
  PRIMARY KEY (`hostName`,`diskName`,`reportTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- ----------------------------
-- Table structure for `node_info`
-- ----------------------------


-- ----------------------------
-- Table structure for `process_info`
-- ----------------------------
DROP TABLE IF EXISTS `process_info`;
CREATE TABLE `process_info` (
  `clusterName` varchar(64) NOT NULL COMMENT '集群名称',
  `hostName` varchar(64) NOT NULL COMMENT '主机名称',
  `managerIp` varchar(64)  NULL DEFAULT '' COMMENT '管理ip（节点）',
  `reportTime` bigint(20) DEFAULT NULL COMMENT '上报时间',
  `processName` varchar(64) NOT NULL DEFAULT '' COMMENT '进程名称',
  `cpuUsage` double DEFAULT NULL COMMENT 'cpu使用率',
  `processCount` int(11) DEFAULT NULL COMMENT '进程数',
  `port` int(11) DEFAULT NULL COMMENT '端口',
  `memoryUsage` double DEFAULT NULL COMMENT '内存使用率',
  `fileHandleTotal` int(11) DEFAULT NULL COMMENT '文件句柄数',
  `threadTotal` int(11) DEFAULT NULL COMMENT '线程总数',
  `type` varchar(64) DEFAULT NULL COMMENT '类型',
  `role` varchar(64) DEFAULT NULL COMMENT '角色：主、备',
  `syn` varchar(64) DEFAULT NULL COMMENT '同步状态',
  `vip` varchar(64) DEFAULT NULL COMMENT 'vip',
  `status` int(11) DEFAULT NULL COMMENT '状态 0:表示正常，1：异常',
  `reserve` varchar(512) DEFAULT NULL COMMENT '保留字段',
  PRIMARY KEY (`clusterName`,`hostName`,`processName`),
  KEY `IDX_NAME` (`hostName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of process_info
-- ----------------------------

-- ----------------------------
-- Table structure for `process_info_history`
-- ----------------------------
DROP TABLE IF EXISTS `process_info_history`;
CREATE TABLE `process_info_history` (
  `clusterName` varchar(64) NOT NULL COMMENT '集群名称',
  `hostName` varchar(64) NOT NULL COMMENT '主机名称',
  `managerIp` varchar(64)  NULL DEFAULT '' COMMENT '管理ip（节点）',
  `reportTime` bigint(20) NOT NULL COMMENT '上报时间',
  `processName` varchar(64) NOT NULL DEFAULT '' COMMENT '进程名称',
  `cpuUsage` double DEFAULT NULL COMMENT 'cpu使用率',
  `processCount` int(11) DEFAULT NULL COMMENT '进程数',
  `port` int(11) DEFAULT NULL COMMENT '端口',
  `memoryUsage` double DEFAULT NULL COMMENT '内存使用率',
  `fileHandleTotal` int(11) DEFAULT NULL COMMENT '文件句柄数',
  `threadTotal` int(11) DEFAULT NULL COMMENT '线程总数',
  `type` varchar(64) DEFAULT NULL COMMENT '类型',
  `role` varchar(64) DEFAULT NULL COMMENT '角色：主、备',
  `syn` varchar(64) DEFAULT NULL COMMENT '同步状态',
  `vip` varchar(64) DEFAULT NULL COMMENT 'vip',
  `status` int(11) DEFAULT NULL COMMENT '状态 0:表示正常，1：异常',
  `reserve` varchar(512) DEFAULT NULL COMMENT '保留字段',
  PRIMARY KEY (`clusterName`,`hostName`,`processName`,`reportTime`),
  KEY `IDX_NAME` (`hostName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of process_info_history
-- ----------------------------
