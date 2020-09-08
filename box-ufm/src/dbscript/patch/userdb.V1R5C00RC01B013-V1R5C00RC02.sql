USE `userdb_0`;
-- ----------------------
-- From PI-1 to PI-2
-- -----------------------
DROP PROCEDURE IF EXISTS alter_inode_add_kiaLabel_sub_table;
DELIMITER $$
CREATE PROCEDURE alter_inode_add_kiaLabel_sub_table()
SQL SECURITY INVOKER
BEGIN
DECLARE aa VARCHAR(256);
DECLARE i INT DEFAULT 0;
WHILE i<500 DO
SET aa=CONCAT('inode_',i);
SET @createobjfiles= CONCAT(' ALTER TABLE ',aa ,' ADD COLUMN `kiaLabel` BIGINT(20) default 0');
PREPARE stmt FROM @createobjfiles;
EXECUTE stmt;
SET i=i+1;
END WHILE;
END $$
DELIMITER ;
CALL alter_inode_add_kiaLabel_sub_table;
DROP PROCEDURE IF EXISTS alter_inode_add_kiaLabel_sub_table;

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