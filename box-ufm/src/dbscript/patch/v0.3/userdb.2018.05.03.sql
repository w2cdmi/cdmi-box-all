DROP TABLE IF EXISTS `inode_share_delete`;
CREATE TABLE `inode_share_delete` (
  `iNodeId` bigint(20) DEFAULT NULL,
  `ownerId` bigint(20) NOT NULL,
  `shareType` varchar(255) DEFAULT NULL,
  `linkCode` varchar(255) DEFAULT NULL,
  `sharedUserId` bigint(20) NOT NULL,
  `sharedUserType` smallint(4) NOT NULL,
  `deleteUserId` bigint(20) NOT NULL,
  KEY `deleteUserId` (`sharedUserId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


DROP TABLE IF EXISTS `inode_share_delete_r`;
CREATE TABLE `inode_share_delete_r` (
  `iNodeId` bigint(20) DEFAULT NULL,
  `ownerId` bigint(20) NOT NULL,
  `shareType` varchar(255) DEFAULT NULL,
  `linkCode` varchar(255) DEFAULT NULL,
  `sharedUserId` bigint(20) NOT NULL,
  `sharedUserType` smallint(4) NOT NULL,
  `deleteUserId` bigint(20) NOT NULL,
  KEY `deleteUserId` (`sharedUserId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
