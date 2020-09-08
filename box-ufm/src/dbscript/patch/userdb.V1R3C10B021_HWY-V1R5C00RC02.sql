use userdb_0;

DROP PROCEDURE IF EXISTS add_inode_link_encryptedPassword;
DELIMITER $$
CREATE PROCEDURE add_inode_link_encryptedPassword()
sql security invoker
BEGIN
    declare aa varchar(256);
    declare i int default 0;
    while i<100 do
        set aa=CONCAT('inode_link_',i);
        set @createobjfiles= CONCAT(' alter table ',aa ,' add `encryptedPassword` varchar(1024) DEFAULT NULL after `password`');
        prepare stmt from @createobjfiles;
        execute stmt;
        set i=i+1;
    end while;
END $$
DELIMITER ;
CALL  add_inode_link_encryptedPassword;
DROP PROCEDURE IF EXISTS add_inode_link_encryptedPassword;

DROP PROCEDURE IF EXISTS add_inode_link_passwordKey;
DELIMITER $$
CREATE PROCEDURE add_inode_link_passwordKey()
sql security invoker
BEGIN
    declare aa varchar(256);
    declare i int default 0;
    while i<100 do
        set aa=CONCAT('inode_link_',i);
        set @createobjfiles= CONCAT(' alter table ',aa ,' add `passwordKey` varchar(2048) DEFAULT NULL after `encryptedPassword`');
        prepare stmt from @createobjfiles;
        execute stmt;
        set i=i+1;
    end while;
END $$
DELIMITER ;
CALL  add_inode_link_passwordKey;
DROP PROCEDURE IF EXISTS add_inode_link_passwordKey;

DROP PROCEDURE IF EXISTS add_inode_link_r_encryptedPassword;
DELIMITER $$
CREATE PROCEDURE add_inode_link_r_encryptedPassword()
sql security invoker
BEGIN
    declare aa varchar(256);
    declare i int default 0;
    while i<100 do
        set aa=CONCAT('inode_link_r_',i);
        set @createobjfiles= CONCAT(' alter table ',aa ,' add `encryptedPassword` varchar(1024) DEFAULT NULL after `password`');
        prepare stmt from @createobjfiles;
        execute stmt;
        set i=i+1;
    end while;
END $$
DELIMITER ;
CALL  add_inode_link_r_encryptedPassword;
DROP PROCEDURE IF EXISTS add_inode_link_r_encryptedPassword;

DROP PROCEDURE IF EXISTS add_inode_link_r_passwordKey;
DELIMITER $$
CREATE PROCEDURE add_inode_link_r_passwordKey()
sql security invoker
BEGIN
    declare aa varchar(256);
    declare i int default 0;
    while i<100 do
        set aa=CONCAT('inode_link_r_',i);
        set @createobjfiles= CONCAT(' alter table ',aa ,' add `passwordKey` varchar(2048) DEFAULT NULL after `encryptedPassword`');
        prepare stmt from @createobjfiles;
        execute stmt;
        set i=i+1;
    end while;
END $$
DELIMITER ;
CALL  add_inode_link_r_passwordKey;
DROP PROCEDURE IF EXISTS add_inode_link_r_passwordKey;

alter table inode_link_dynamic add `encryptedPassword` varchar(1024) DEFAULT NULL after `password`;
alter table inode_link_dynamic add `passwordKey` varchar(2048) DEFAULT NULL after `encryptedPassword`;

-- for full disk backup function--
DROP PROCEDURE IF EXISTS proc_copy_temp_inode_no_backup;
DELIMITER $$
CREATE PROCEDURE proc_copy_temp_inode_no_backup(IN srcTableSuffix int(11), IN userid bigint(20), IN destTableSuffix bigint(20))
SQL SECURITY INVOKER
BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE bb VARCHAR(256);

    SET aa=CONCAT('syncdb.inode_',userid,'_',destTableSuffix);
    SET bb=CONCAT('inode_',srcTableSuffix);

    SET @sqlstring= CONCAT(' CREATE TABLE ',aa,' like inode_0');
    PREPARE stmt FROM @sqlstring;
    EXECUTE stmt;

    SET @sqlstring= CONCAT(' insert into ',aa,' select * from ', bb, ' where ownedBy=', userid, ' and status=0 and type!=2 and syncStatus!=3');
    PREPARE stmt FROM @sqlstring;
    EXECUTE stmt;
END $$
DELIMITER ;


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

DROP PROCEDURE IF EXISTS create_mirror_object_subtable;
DELIMITER $$
CREATE PROCEDURE create_mirror_object_subtable()
sql security invoker
BEGIN
    declare aa varchar(256);
    declare i int default 1;
    while i<500 do
        set aa=CONCAT('mirror_object_',i);
        set @createobjfiles= CONCAT(' create table ',aa ,' like mirror_object_0');
        prepare stmt from @createobjfiles;
        execute stmt;
        set i=i+1;
    end while;
END $$
DELIMITER ;
CALL  create_mirror_object_subtable;
