USE `userdb_0`;

DROP PROCEDURE IF EXISTS create_inode_link_sub_table;
DELIMITER $$
CREATE PROCEDURE create_inode_link_sub_table()
sql security invoker
BEGIN
    declare aa varchar(256);
    declare i int default 1;
    while i<100 do
        set aa=CONCAT('inode_link_',i);
        set @createobjfiles= CONCAT(' create table ',aa ,' like inode_link_0');
        prepare stmt from @createobjfiles;
        execute stmt;
        set i=i+1;
    end while;
END $$
DELIMITER ;
CALL  create_inode_link_sub_table;
DROP PROCEDURE IF EXISTS create_inode_link_sub_table;

DROP PROCEDURE IF EXISTS create_inode_link_r_sub_table;
DELIMITER $$
CREATE PROCEDURE create_inode_link_r_sub_table()
sql security invoker
BEGIN
    declare aa varchar(256);
    declare i int default 1;
    while i<100 do
        set aa=CONCAT('inode_link_r_',i);
        set @createobjfiles= CONCAT(' create table ',aa ,' like inode_link_r_0');
        prepare stmt from @createobjfiles;
        execute stmt;
        set i=i+1;
    end while;
END $$
DELIMITER ;
CALL  create_inode_link_r_sub_table;
DROP PROCEDURE IF EXISTS create_inode_link_r_sub_table;

DROP PROCEDURE IF EXISTS create_inode_share_sub_table;
DELIMITER $$
CREATE PROCEDURE create_inode_share_sub_table()
sql security invoker
BEGIN
    declare aa varchar(256);
    declare i int default 1;
    while i<100 do
        set aa=CONCAT('inode_share_',i);
        set @createsharefolders= CONCAT(' create table ',aa ,' like inode_share_0');
        prepare stmt from @createsharefolders;
        execute stmt;
        set i=i+1;
    end while;
END $$
DELIMITER ;
CALL  create_inode_share_sub_table;
DROP PROCEDURE IF EXISTS create_inode_share_sub_table;

DROP PROCEDURE IF EXISTS create_inode_share_r_sub_table;
DELIMITER $$
CREATE PROCEDURE create_inode_share_r_sub_table()
sql security invoker
BEGIN
    declare aa varchar(256);
    declare i int default 0;
    while i<100 do
        set aa=CONCAT('inode_share_r_',i);
        set @createsharefolders= CONCAT(' create table ',aa ,' like inode_share_0');
        prepare stmt from @createsharefolders;
        execute stmt;
        set i=i+1;
    end while;
END $$
DELIMITER ;
CALL  create_inode_share_r_sub_table;
DROP PROCEDURE IF EXISTS create_inode_share_r_sub_table;