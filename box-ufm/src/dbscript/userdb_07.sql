USE `userdb_0`;
--

DROP PROCEDURE IF EXISTS drop_inode_acl_sub_table;
DELIMITER $$
CREATE PROCEDURE drop_inode_acl_sub_table()
SQL SECURITY INVOKER
BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 0;
    WHILE i<100 DO
        SET aa=CONCAT('inode_acl_',i);
        SET @createobjfiles= CONCAT(' DROP TABLE IF EXISTS ',aa);
        PREPARE stmt FROM @createobjfiles;
        EXECUTE stmt;
        SET i=i+1;
    END WHILE;
END $$
DELIMITER ;
CALL  drop_inode_acl_sub_table;
DROP PROCEDURE IF EXISTS drop_inode_acl_sub_table;

--
-- Table structure for table `inode_acl_0`
--
DROP TABLE IF EXISTS `inode_acl_0`;
CREATE TABLE `inode_acl_0` (
  `id` bigint(20) NOT NULL,
  `ownedBy` bigint(20) NOT NULL,
  `iNodeId` bigint(20) NOT NULL,
  `iNodePid` bigint(20) NOT NULL,
  `accessUserId` varchar(32) NOT NULL,
  `userType` varchar(32) NOT NULL,
  `resourceRole` varchar(255) NOT NULL,
  `createdBy` bigint(20) NOT NULL,
  `createdAt` datetime DEFAULT NULL,
  `modifiedBy` bigint(20) DEFAULT NULL,
  `modifiedAt` datetime DEFAULT NULL,
  KEY `idx_ownedBy_id` (`ownedBy`,`id`),
  KEY `idx_acl` (`ownedBy`, `accessUserId`, `userType`),
  UNIQUE KEY `memberships` (`ownedBy`,`iNodeId`,`accessUserId`,`userType`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP PROCEDURE IF EXISTS create_inode_acl_sub_table;
DELIMITER $$
CREATE PROCEDURE create_inode_acl_sub_table()
sql security invoker
BEGIN
    declare aa varchar(256);
    declare i int default 1;
    while i<100 do
        set aa=CONCAT('inode_acl_',i);
        set @createobjfiles= CONCAT(' create table ',aa ,' like inode_acl_0');
        prepare stmt from @createobjfiles;
        execute stmt;
        set i=i+1;
    end while;
END $$
DELIMITER ;
CALL  create_inode_acl_sub_table;
DROP PROCEDURE IF EXISTS create_inode_acl_sub_table;
