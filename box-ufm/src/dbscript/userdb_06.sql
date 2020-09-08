USE `userdb_0`;

DROP PROCEDURE IF EXISTS create_inode_delete_sub_table;
DELIMITER $$
CREATE PROCEDURE create_inode_delete_sub_table()
sql security invoker
BEGIN
    declare aa varchar(256);
    declare i int default 1;
    while i<500 do
        set aa=CONCAT('inode_delete_',i);
        set @createobjfiles= CONCAT(' create table ',aa ,' like inode_delete_0');
        prepare stmt from @createobjfiles;
        execute stmt;
        set i=i+1;
    end while;
END $$
DELIMITER ;
CALL  create_inode_delete_sub_table;
DROP PROCEDURE IF EXISTS create_inode_delete_sub_table;

















