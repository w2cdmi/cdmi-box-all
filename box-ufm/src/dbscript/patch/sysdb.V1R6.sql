USE `sysdb`;

/* 在user表中增加字段*/
DROP PROCEDURE IF EXISTS update_user_sub_table;
DELIMITER $$
CREATE PROCEDURE update_user_sub_table()
  SQL SECURITY INVOKER
  BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 0;
    WHILE i<100 DO
      SET aa=CONCAT('user_',i);
      SET @alertobjfiles= CONCAT('ALTER TABLE ',aa,' ADD COLUMN `versionFileSize` bigint NOT NULL DEFAULT -1;');
      SET @alertobjfiles1= CONCAT('ALTER TABLE ',aa,' ADD COLUMN `versionFileType`  varchar(256) NULL;');
      PREPARE stmt FROM @alertobjfiles;
      PREPARE stmt1 FROM @alertobjfiles1;
      EXECUTE stmt;
      EXECUTE stmt1;
      SET i=i+1;
    END WHILE;
  END $$
DELIMITER ;
CALL  update_user_sub_table;

/* 在account_user表中增加字段*/
DROP PROCEDURE IF EXISTS update_account_user_sub_table;
DELIMITER $$
CREATE PROCEDURE update_account_user_sub_table()
  SQL SECURITY INVOKER
  BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 0;
    WHILE i<100 DO
      SET aa=CONCAT('account_user_',i);
      SET @alertobjfiles= CONCAT('ALTER TABLE ',aa,' ADD COLUMN `versionFileSize` bigint NOT NULL DEFAULT -1;');
      SET @alertobjfiles1= CONCAT('ALTER TABLE ',aa,' ADD COLUMN `versionFileType`  varchar(256) NULL;');
      PREPARE stmt FROM @alertobjfiles;
      PREPARE stmt1 FROM @alertobjfiles1;
      EXECUTE stmt;
      EXECUTE stmt1;
      SET i=i+1;
    END WHILE;
  END $$
DELIMITER ;
CALL  update_account_user_sub_table;

alter table teamspace add type int(11) default 0 after name;

USE `userdb_0`;

/* 在inode_link_r_##表中增加字段*/
DROP PROCEDURE IF EXISTS update_inode_link_r;
DELIMITER $$
CREATE PROCEDURE update_inode_link_r()
  SQL SECURITY INVOKER
  BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 0;
    WHILE i<100 DO
      SET aa=CONCAT('inode_link_r_',i);
      SET @alertobjfiles= CONCAT('ALTER TABLE ',aa,' ADD COLUMN `isAnon` boolean NOT NULL DEFAULT false;');
      PREPARE stmt FROM @alertobjfiles;
      EXECUTE stmt;
      SET i=i+1;
    END WHILE;
  END $$
DELIMITER ;
CALL  update_inode_link_r;

/* 在inode_link_##表中增加字段*/
DROP PROCEDURE IF EXISTS update_inode_link;
DELIMITER $$
CREATE PROCEDURE update_inode_link()
  SQL SECURITY INVOKER
  BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 0;
    WHILE i<100 DO
      SET aa=CONCAT('inode_link_',i);
      SET @alertobjfiles= CONCAT('ALTER TABLE ',aa,' ADD COLUMN `isAnon` boolean NOT NULL DEFAULT false;');
      PREPARE stmt FROM @alertobjfiles;
      EXECUTE stmt;
      SET i=i+1;
    END WHILE;
  END $$
DELIMITER ;
CALL  update_inode_link;
