USE `userdb_0`;

DROP PROCEDURE IF EXISTS create_preview_object_sub_table;
DELIMITER $$
CREATE PROCEDURE create_preview_object_sub_table()
sql security invoker
BEGIN
    declare aa varchar(256);
    declare i int default 1;
    while i<100 do
        set aa=CONCAT('preview_object_',i);
        set @createobj= CONCAT(' create table ',aa ,' like preview_object_0');
        prepare stmt from @createobj;
        execute stmt;
        set i=i+1;
    end while;
END $$
DELIMITER ;
CALL  create_preview_object_sub_table;
DROP PROCEDURE IF EXISTS create_preview_object_sub_table;

USE `userdb_0`;

DROP TABLE IF EXISTS `object_mirror_ship_0`;
CREATE TABLE `object_mirror_ship_0` (
  `objectId` varchar(32) NOT NULL,
  `size` bigint(20) DEFAULT NULL,
  `parentObjectId` varchar(32) NOT NULL,
  `resourceGroupId` int(11) NOT NULL,
  `createdAt` datetime DEFAULT NULL,
  `accessedAt` datetime DEFAULT NULL,
  `type` int(11) NOT NULL,
  PRIMARY KEY (`objectId`),
  KEY `PARENTOBJECTID` (`parentObjectId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 

DROP PROCEDURE IF EXISTS create_object_mirror_ship_subtable;
DELIMITER $$
CREATE PROCEDURE create_object_mirror_ship_subtable()
sql security invoker
BEGIN
    declare aa varchar(256);
    declare i int default 1;
    while i<500 do
        set aa=CONCAT('object_mirror_ship_',i);
        set @createobjfiles= CONCAT(' create table ',aa ,' like object_mirror_ship_0');
        prepare stmt from @createobjfiles;
        execute stmt;
        set i=i+1;
    end while;
END $$
DELIMITER ;
CALL  create_object_mirror_ship_subtable;



DROP TABLE IF EXISTS `object_secretLevel_0`;
CREATE TABLE `object_secretLevel_0` (
  `sha1` varchar(64) NOT NULL,
  `regionId` int(11) DEFAULT NULL,
  `accountId` int(11) DEFAULT NULL,
  `secretLevel` int(2) DEFAULT NULL,
  KEY `idx_sha1` (`sha1`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP PROCEDURE IF EXISTS create_objectSecretLevel_sub_table;


DELIMITER $$
CREATE PROCEDURE create_objectSecretLevel_sub_table()
sql security invoker
BEGIN
    declare aa varchar(256);
    declare i int default 1;
    while i<100 do
        set aa=CONCAT('object_secretLevel_',i);
        set @createobjfiles= CONCAT(' create table ',aa ,' like object_secretLevel_0');
        prepare stmt from @createobjfiles;
        execute stmt;
        set i=i+1;
    end while;
END $$
DELIMITER ;
CALL  create_objectSecretLevel_sub_table;
DROP PROCEDURE IF EXISTS create_objectSecretLevel_sub_table;

#
# DELIMITER $$
# CREATE PROCEDURE create_recentBrowse_sub_table()
# sql security invoker
# BEGIN
#     declare aa varchar(256);
#     declare i int default 1;
#     while i<100 do
#         set aa=CONCAT('recent_browse_',i);
#         set @createobjfiles= CONCAT(' create table ',aa ,' like recent_browse_0');
#         prepare stmt from @createobjfiles;
#         execute stmt;
#         set i=i+1;
#     end while;
# END $$
# DELIMITER ;
# CALL  create_recentBrowse_sub_table;
# DROP PROCEDURE IF EXISTS create_recentBrowse_sub_table;
#
# DELIMITER $$
# CREATE PROCEDURE create_recentBrowse_r_sub_table()
# sql security invoker
# BEGIN
#     declare aa varchar(256);
#     declare i int default 1;
#     while i<100 do
#         set aa=CONCAT('recent_browse_r_',i);
#         set @createobjfiles= CONCAT(' create table ',aa ,' like recent_browse_0');
#         prepare stmt from @createobjfiles;
#         execute stmt;
#         set i=i+1;
#     end while;
# END $$
# DELIMITER ;
# CALL  create_recentBrowse_r_sub_table;
# DROP PROCEDURE IF EXISTS create_recentBrowse_r_sub_table;

