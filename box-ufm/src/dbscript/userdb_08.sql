CREATE DATABASE IF NOT EXISTS `syncdb`;
USE `userdb_0`;


-- ----------------------------
-- Procedure structure for proc_copy_temp_inode
-- ----------------------------
DROP PROCEDURE IF EXISTS proc_copy_temp_inode;
DELIMITER $$
CREATE PROCEDURE proc_copy_temp_inode(IN srcTableSuffix int(11), IN userid bigint(20), IN destTableSuffix bigint(20))
SQL SECURITY INVOKER
BEGIN
	DECLARE aa VARCHAR(256);
	DECLARE bb VARCHAR(256);

	SET aa=CONCAT('syncdb.inode_',userid,'_',destTableSuffix);
	SET bb=CONCAT('inode_',srcTableSuffix);

    SET @sqlstring= CONCAT(' CREATE TABLE ',aa,' like inode_0');
    PREPARE stmt FROM @sqlstring;
    EXECUTE stmt;

    SET @sqlstring= CONCAT(' insert into ',aa,' select * from ', bb, ' where ownedBy=', userid, ' and status=0 and type!=2');
    PREPARE stmt FROM @sqlstring;
    EXECUTE stmt;
END $$
DELIMITER ;


-- ----------------------------
-- Procedure structure for proc_copy_temp_inode_no_backup
-- ----------------------------
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



-- ----------------------------
-- Procedure structure for proc_drop_temp_inode
-- ----------------------------
DROP PROCEDURE IF EXISTS proc_drop_temp_inode;
DELIMITER $$
CREATE PROCEDURE proc_drop_temp_inode(IN destTableSuffix BIGINT(20), IN userid BIGINT(20))
SQL SECURITY INVOKER
BEGIN
	DECLARE aa VARCHAR(256);
	
	SET aa=CONCAT('syncdb.inode_',userid,'_',destTableSuffix);
	SET @sqlstring= CONCAT(' DROP TABLE IF EXISTS ',aa);
    PREPARE stmt FROM @sqlstring;
    EXECUTE stmt;	
END $$
DELIMITER ;

-- ----------------------------
-- Procedure structure for proc_get_temp_inode
-- ----------------------------
DROP PROCEDURE IF EXISTS proc_get_temp_inode;
DELIMITER $$
CREATE PROCEDURE proc_get_temp_inode(IN destTableSuffix BIGINT(20), IN userid BIGINT(20), IN datalen INT(11), IN offset BIGINT(20))
SQL SECURITY INVOKER
BEGIN
	DECLARE aa VARCHAR(256);
	SET aa=CONCAT('syncdb.inode_',userid,'_',destTableSuffix);
	
	SET @sqlstring= CONCAT(' select * from  ',aa, ' where ownedBy=', userid, ' limit ', offset, ',', datalen);
    PREPARE stmt FROM @sqlstring;
    EXECUTE stmt;
END $$
DELIMITER ;

-- ----------------------------
-- Procedure structure for proc_create_sub_message
-- ----------------------------
DROP PROCEDURE IF EXISTS proc_create_sub_message;
DELIMITER $$
CREATE PROCEDURE proc_create_sub_message()
sql security invoker
BEGIN
    declare tableName varchar(256);
    declare i int default 1;
    while i<10 do
        set tableName=CONCAT('message_',i);
        set @createSql= CONCAT('create table ',tableName ,' like message_0');
        prepare stmt from @createSql;
        execute stmt;
        set i=i+1;
    end while;
END $$
DELIMITER ;
CALL  proc_create_sub_message;
DROP PROCEDURE IF EXISTS proc_create_sub_message;