USE `userdb_0`;

DROP PROCEDURE IF EXISTS create_object_fp_sub_table;
DELIMITER $$
CREATE PROCEDURE create_object_fp_sub_table()
sql security invoker
BEGIN
    declare aa varchar(256);
    declare i int default 1;
    while i<500 do
        set aa=CONCAT('object_fp_',i);
        set @createobj= CONCAT(' create table ',aa ,' like object_fp_0');
        prepare stmt from @createobj;
        execute stmt;
        set i=i+1;
    end while;
END $$
DELIMITER ;
CALL  create_object_fp_sub_table;
DROP PROCEDURE IF EXISTS create_object_fp_sub_table;


