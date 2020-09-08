CREATE DATABASE  IF NOT EXISTS `userdb_0`;
USE `userdb_0`;

drop table if exists db_version;
CREATE TABLE `db_version` (                            
    `version` varchar(128) NOT NULL,                     
    `modified` date NOT NULL,                            
    `description` varchar(2048) NOT NULL,                
    `sequence` int(11) NOT NULL AUTO_INCREMENT,          
    PRIMARY KEY (`version`),
    KEY `sequence` (`sequence`)                                     
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;
insert into db_version(version, modified, description) values('1.3.00.2901', '2014-9-25', '');

-- ----------------------------
-- Table structure for `inode_favorite`
-- ----------------------------
DROP TABLE IF EXISTS `inode_favorite`;
CREATE TABLE `inode_favorite` (
`id`  bigint(20) NOT NULL,
`ownedBy`  bigint(20) NOT NULL ,
`type`  smallint(4) NOT NULL ,
`parent`  bigint(20) NOT NULL ,
`name`  varchar(255)   NOT NULL ,
`createdAt`  datetime NOT NULL ,
`modifiedAt`  datetime  DEFAULT NULL ,
`nodeOwnedBy`  bigint(20)  DEFAULT NULL ,
`nodeId`   bigint(20)   DEFAULT NULL ,
`nodeType`  tinyint(4) DEFAULT NULL ,
`params`  text DEFAULT NULL ,
UNIQUE KEY `UQ_owner_id` (`ownedBy`,`id`),
INDEX `ownedBy_id` USING BTREE (`ownedBy`) 
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `user_custom`
--
DROP TABLE IF EXISTS `user_custom`;
CREATE TABLE `user_custom` (
  `id` bigint(20) NOT NULL,
  `language` varchar(255) NOT NULL,
  `datePattern` varchar(255) NOT NULL,
  `timePattern` varchar(255) NOT NULL,
  `timeZone` varchar(255) NOT NULL,
  `avatar` blob,
  PRIMARY KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `user_config`
--
DROP TABLE IF EXISTS `user_config`;
CREATE TABLE `user_config` (
  `configId` varchar(128) NOT NULL,
  `userId` bigint(20) NOT NULL,
  `value` varchar(255) NOT NULL,
  KEY `config_id` (`configId`),
  KEY `user_id` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `user_ext_attr`
--
DROP TABLE IF EXISTS `user_ext_attr`;
CREATE TABLE `user_ext_attr` (
  `extAttrId` varchar(128) NOT NULL,
  `userId` bigint(20) NOT NULL,
  `value` text NOT NULL,
  KEY `ext_attr_id` (`extAttrId`),
  KEY `user_id` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `user_permission`
--
DROP TABLE IF EXISTS `user_permission`;
CREATE TABLE `user_permission` (
  `permissionId` varchar(128) NOT NULL,
  `userId` bigint(20) NOT NULL,
  `value` varchar(255) NOT NULL,
  KEY `permission_id` (`permissionId`),
  KEY `user_id` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `inode_0`
--
DROP TABLE IF EXISTS `inode_0`;
CREATE TABLE `inode_0` (
  `id` bigint(20) NOT NULL,
  `parentId` bigint(20) NOT NULL,
  `objectId` varchar(32) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `size` bigint(20) NOT NULL,
  `description` text,
  `type` tinyint(4) NOT NULL,
  `status` tinyint(4) NOT NULL,
  `version` varchar(32) DEFAULT NULL,
  `ownedBy` bigint(20) NOT NULL,
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  `contentCreatedAt` datetime DEFAULT NULL,
  `contentModifiedAt` datetime DEFAULT NULL,
  `createdBy` bigint(20) NOT NULL,
  `modifiedBy` bigint(20) NOT NULL,
  `shareStatus` tinyint(4) NOT NULL,
  `syncStatus` tinyint(4) NOT NULL,
  `syncVersion` bigint(20) NOT NULL,
  `linkCode` varchar(32) DEFAULT NULL,
  `encryptKey` varchar(256) DEFAULT NULL,
  `sha1` varchar(64) NOT NULL,
  `securityId` tinyint(4) DEFAULT 0,
  `resourceGroupId` int(11) NOT NULL,
  `kiaLabel` BIGINT(20) default 0,
  `doctype` int(5) NOT NULL DEFAULT '5',
  `folderType` varchar(32) DEFAULT "",
  KEY `idx_ownedBy` (`ownedBy`),
  KEY `idx_owner_parent` (`ownedBy`,`parentId`),
  KEY `idx_owner_syncversion` (`ownedBy`,`syncVersion`),
  KEY `idx_object` (`objectId`),
  KEY `doctype` (`doctype`),
  UNIQUE KEY `UQ_owner_id` (`ownedBy`,`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table structure for table `mirror_object_0`
DROP TABLE IF EXISTS `mirror_object_0`;
CREATE TABLE `mirror_object_0` (
  `ownedBy` bigint(20) NOT NULL,
  `srcObjectId` varchar(32) NOT NULL,
  `srcResourceGroupId` int(11) NOT NULL,
  `destObjectId` varchar(32) NOT NULL,
  `destResourceGroupId` int(11) NOT NULL,
  `createAt` datetime NOT NULL,
  `policyId` int(11) NOT NULL,
  `type` int(11) NOT NULL,
  PRIMARY KEY (`ownedBy`,`srcObjectId`,`destObjectId`),
  KEY `destObjectId` (`destObjectId`),
  KEY `ownedBy` (`ownedBy`),
  KEY `srcObjectId` (`srcObjectId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `inode_link_0`
--
DROP TABLE IF EXISTS `inode_link_0`;
CREATE TABLE `inode_link_0` (
  `id` varchar(32) NOT NULL,
  `iNodeId` bigint(20) NOT NULL,
  `ownedBy` bigint(20) NOT NULL,
  `url` varchar(255) DEFAULT NULL,
  `downloadUrl` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `encryptedPassword` varchar(1024) DEFAULT NULL,
  `passwordKey` varchar(2048) DEFAULT NULL,
  `access` tinyint(4) DEFAULT 0,
  `role` varchar(20) DEFAULT NULL,
  `effectiveAt` datetime DEFAULT NULL,
  `expireAt` datetime DEFAULT NULL,
  `createdAt` datetime NOT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  `createdBy` bigint(20) NOT NULL,
  `modifiedBy` bigint(20) DEFAULT NULL,
  `status` tinyint(4) DEFAULT 1,
  `isAnon` boolean NOT NULL DEFAULT false,
  UNIQUE KEY `UQ_inode_link_id` (`id`),
  KEY `inode_link_inode_id` (`iNodeId`),
  KEY `inode_link_owned_by` (`ownedBy`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


--
-- Table structure for table `inode_link_r_0`
--
DROP TABLE IF EXISTS `inode_link_r_0`;
CREATE TABLE `inode_link_r_0` (
  `id` varchar(32) NOT NULL,
  `iNodeId` bigint(20) NOT NULL,
  `ownedBy` bigint(20) NOT NULL,
  `url` varchar(255) DEFAULT NULL,
  `downloadUrl` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `encryptedPassword` varchar(1024) DEFAULT NULL,
  `passwordKey` varchar(2048) DEFAULT NULL,
  `access` tinyint(4) DEFAULT 0,
  `role` varchar(20) DEFAULT NULL,
  `effectiveAt` datetime DEFAULT NULL,
  `expireAt` datetime DEFAULT NULL,
  `createdAt` datetime NOT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  `createdBy` bigint(20) NOT NULL,
  `modifiedBy` bigint(20) DEFAULT NULL,
  `status` tinyint(4) DEFAULT 1,
  `isAnon` boolean NOT NULL DEFAULT false,
  UNIQUE KEY `UQ_inode_link_id` (`id`),
  KEY `inode_link_owned_node` (`ownedBy`,`iNodeId`),
  KEY `inode_link_owned_by` (`ownedBy`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `inode_share_0`
--

DROP TABLE IF EXISTS `inode_share_0`;
CREATE TABLE `inode_share_0` (
  `id` bigint(16) default NULL,
  `iNodeId` bigint(20) NOT NULL,
  `type` smallint(3) NOT NULL,
  `name` varchar(255) NOT NULL,
  `ownerId` bigint(20) NOT NULL,
  `ownerName` varchar(255) NOT NULL,
  `sharedUserId` bigint(20) NOT NULL,
  `sharedUserType` smallint(4) DEFAULT 0,
  `sharedUserName` varchar(255),
  `createdAt` datetime NOT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  `createdBy` bigint(20) NOT NULL,
  `modifiedBy` bigint(20) DEFAULT NULL,
  `roleName` varchar(16) NOT NULL,
  `status` smallint(4) DEFAULT 0,
  `size` bigint(20),
  `originalNodeId` bigint(20) DEFAULT NULL,
  `originalOwnerId` bigint(20) DEFAULT NULL,
  `originalType` smallint(3) DEFAULT NULL,
  KEY `inode_share_inode_id` (`iNodeId`),
  KEY `inode_share_owned_id` (`ownerId`),
  KEY `inode_share_user_id` (`sharedUserId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



--
-- Table structure for table `object_fp_0`
--
DROP TABLE IF EXISTS `object_fp_0`;
CREATE TABLE `object_fp_0` (
  `id` varchar(32) NOT NULL,
  `sha1` varchar(64) NOT NULL,
  `regionId` int(11) DEFAULT NULL,
  KEY `idx_sha1` (`sha1`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `object_reference_0`
--
DROP TABLE IF EXISTS `object_reference_0`;
CREATE TABLE `object_reference_0` (
  `id` varchar(32) NOT NULL,
  `sha1` varchar(64) NOT NULL,
  `blockMD5` varchar(64) DEFAULT NULL,
  `lastDeleteTime` datetime DEFAULT NULL,
  `size` bigint(20) DEFAULT NULL,
  `refCount` int(11) DEFAULT NULL,
  `resourceGroupId` int(11) NOT NULL,
  `securityLabel` int(11) NOT NULL DEFAULT 0,
  `securityVersion` varchar(32) NOT NULL DEFAULT "",
  UNIQUE KEY `UQ_object_reference_id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `preview_object_0`
--
DROP TABLE IF EXISTS `preview_object_0`;
CREATE TABLE `preview_object_0` (
  `sourceObjectId` varchar(32) NOT NULL,
  `accountId` bigint(20) NOT NULL,
  `convertStartTime` datetime NOT NULL,
  `createdAt` datetime DEFAULT NULL,
  `status` tinyint(4) NOT NULL,
  `storageObjectId` varchar(32) DEFAULT NULL,
  `resourceGroupId` int(11) NOT NULL,
  `size` bigint(20) NOT NULL,
  `md5` varchar(64) DEFAULT NULL,
  `blockMD5` varchar(64) DEFAULT NULL,
  UNIQUE KEY `UQ_preview_object_index` (`sourceObjectId`,`accountId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `inode_0`
--
DROP TABLE IF EXISTS `inode_delete_0`;
CREATE TABLE `inode_delete_0` (
  `id` bigint(20) NOT NULL,
  `parentId` bigint(20) NOT NULL,
  `objectId` varchar(32) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `size` bigint(20) NOT NULL,
  `description` text,
  `type` tinyint(4) NOT NULL,
  `status` tinyint(4) NOT NULL,
  `version` varchar(32) DEFAULT NULL,
  `ownedBy` bigint(20) NOT NULL,
  `createdAt` datetime DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  `contentCreatedAt` datetime DEFAULT NULL,
  `contentModifiedAt` datetime DEFAULT NULL,
  `createdBy` bigint(20) NOT NULL,
  `modifiedBy` bigint(20) NOT NULL,
  `shareStatus` tinyint(4) NOT NULL,
  `syncStatus` tinyint(4) NOT NULL,
  `syncVersion` bigint(20) NOT NULL,
  `linkCode` varchar(32) DEFAULT NULL,
  `encryptKey` varchar(256) DEFAULT NULL,
  `sha1` varchar(64) NOT NULL,
  `resourceGroupID` int(11) NOT NULL,
  KEY `idx_owner_id` (`ownedBy`,`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `inode_mail_msg`
--
DROP TABLE IF EXISTS `inode_mail_msg`;
CREATE TABLE `inode_mail_msg` (
  `userId` bigint(20) NOT NULL,
  `source` varchar(32) NOT NULL,
  `ownerId` bigint(20) NOT NULL,
  `nodeId` bigint(20) NOT NULL,
  `subject` varchar(255) DEFAULT NULL,
  `message` TEXT,
 UNIQUE KEY `idx_userId_ownerId_iNodeId` (`userId`,`source`,`ownerId`,`nodeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `message_0`
--
DROP TABLE IF EXISTS `message_0`;
CREATE TABLE `message_0` (
  `id` bigint(20) NOT NULL,
  `providerId` bigint(20) NOT NULL,
  `receiverId` bigint(20) NOT NULL,
  `appId` varchar(64) NOT NULL,
  `params` varchar(512) DEFAULT NULL,
  `type` tinyint(4) NOT NULL,
  `status` tinyint(4) NOT NULL,
  `createdAt` datetime NOT NULL,
  `expiredAt` datetime NOT NULL,
  KEY `idx_receiverId` (`receiverId`),
  KEY `idx_receiverId_status` (`receiverId`,`status`),
  UNIQUE KEY `uq_receiverId_messageId` (`receiverId`,`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `inode_link_dynamic`;
CREATE TABLE `inode_link_dynamic` (
  `id` varchar(32) NOT NULL,
  `identity` varchar(128) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `encryptedPassword` varchar(1024) DEFAULT NULL,
  `passwordKey` varchar(2048) DEFAULT NULL,
  `createdAt` datetime NOT NULL,
  `expiredAt` datetime DEFAULT NULL,
  UNIQUE KEY `UQ_inode_link_id` (`id`, `identity`),
  KEY `idx_id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


use `userdb_0`;
DROP TABLE IF EXISTS `filelabel_link_0`;
CREATE TABLE `filelabel_link_0` (
	`id` BIGINT(20) NOT NULL COMMENT '主鍵',
	`labelId` BIGINT(20) NOT NULL COMMENT '標簽編號',
	`inodeId` BIGINT(20) NOT NULL COMMENT '文件編號',
	`createBy` BIGINT(20) NOT NULL COMMENT '綁定者',
	`ownerId` BIGINT(20) NOT NULL COMMENT '所屬用戶',
	`bindTime` datetime COMMENT '綁定時間',
	PRIMARY KEY (`id`),
	UNIQUE KEY `uq_inode_label_key` (`inodeId`,`labelId`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

USE `userdb_0`;
DROP PROCEDURE IF EXISTS create_filelabel_link_sub_table;
DELIMITER $$
CREATE PROCEDURE create_filelabel_link_sub_table()
sql security invoker
BEGIN
	declare aa varchar(256);
	declare i int default 1;
	while i<500 do
		set aa=CONCAT('filelabel_link_',i);
		set @createobjfiles= CONCAT(' create table ',aa ,' like filelabel_link_0');
		prepare stmt from @createobjfiles;
		execute stmt;
		set i=i+1;
	end while;
END $$
DELIMITER ;
CALL  create_filelabel_link_sub_table;
DROP PROCEDURE IF EXISTS create_filelabel_link_sub_table;

-- ----------------------------
DROP TABLE IF EXISTS `recent_browse`;
CREATE TABLE `recent_browse` (
  `lastBrowseTime` datetime DEFAULT NULL,
  `inodeId` bigint(20) DEFAULT NULL,
  `ownedBy` bigint(20) DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  `accountId` bigint(20) DEFAULT NULL,
  `status` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
DROP TABLE IF EXISTS `inode_link_approve`;
CREATE TABLE `inode_link_approve` (
  `linkCode` varchar(128) DEFAULT NULL,
  `status` tinyint(2) DEFAULT NULL,
  `approveBy` bigint(20) DEFAULT NULL,
  `approveName` varchar(255) DEFAULT NULL,
  `startTime` datetime DEFAULT NULL,
  `endTime` datetime DEFAULT NULL,
  `accountId` varchar(20) DEFAULT NULL,
  `applyReason` varchar(255) DEFAULT NULL,
  `approveAt` datetime DEFAULT NULL,
  `nodeId` bigint(20) DEFAULT NULL,
  `nodeName` varchar(255) DEFAULT NULL,
  `linkOwner` bigint(20) DEFAULT NULL,
  `linkOwnerName` varchar(255) DEFAULT NULL,
  `linkStatus` tinyint(2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ----------------------------
-- Table structure for `link_approve_user`
-- ----------------------------
DROP TABLE IF EXISTS `link_approve_user`;
CREATE TABLE `link_approve_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cloudUserId` bigint(20) NOT NULL,
  `linkCode` varchar(128) NOT NULL,
  `type` tinyint(4) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;



DROP TABLE IF EXISTS `inode_link_approve_record`;
CREATE TABLE `inode_link_approve_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `linkCode` varchar(128) NOT NULL,
  `approveAt` datetime NOT NULL,
  `approveBy` bigint(20) NOT NULL,
  `status` tinyint(2) NOT NULL,
  `comment` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `inode_link_approve_record_linkCode` (`linkCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


DROP TABLE IF EXISTS `inode_link_approve`;
CREATE TABLE `inode_link_approve` (
  `linkCode` varchar(128) DEFAULT NULL,
  `status` tinyint(2) DEFAULT NULL,
  `approveBy` bigint(20) DEFAULT NULL,
  `approveName` varchar(255) DEFAULT NULL,
  `startTime` datetime DEFAULT NULL,
  `endTime` datetime DEFAULT NULL,
  `accountId` varchar(20) DEFAULT NULL,
  `applyReason` varchar(255) DEFAULT NULL,
  `approveAt` datetime DEFAULT NULL,
  `nodeId` bigint(20) DEFAULT NULL,
  `nodeName` varchar(255) DEFAULT NULL,
  `linkOwner` bigint(20) DEFAULT NULL,
  `linkOwnerName` varchar(255) DEFAULT NULL,
  `linkStatus` tinyint(2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ----------------------------
-- Table structure for `link_approve_user`
-- ----------------------------
DROP TABLE IF EXISTS `link_approve_user`;
CREATE TABLE `link_approve_user` (
  `cloudUserId` bigint(20) NOT NULL,
  `linkCode` varchar(128) NOT NULL,
  `type` tinyint(4) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for `inode_shortcut`
-- ----------------------------
DROP TABLE IF EXISTS `inode_shortcut`;
CREATE TABLE `inode_shortcut` (
  `id` bigint(20) DEFAULT NULL,
  `nodeId` bigint(20) DEFAULT NULL,
  `ownerId` bigint(20) DEFAULT NULL,
  `createBy` bigint(20) DEFAULT NULL,
  `createAt` datetime DEFAULT NULL,
  `type` tinyint(2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of inode_shortcut
-- ----------------------------
