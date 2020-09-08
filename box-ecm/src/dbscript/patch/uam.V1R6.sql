use uam;


DROP PROCEDURE IF EXISTS update_enterprise_user_sub_table;
DELIMITER $$
CREATE PROCEDURE update_enterprise_user_sub_table()
  SQL SECURITY INVOKER
  BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 0;
    WHILE i<100 DO
      SET aa=CONCAT('enterprise_user_',i);
      SET @alertobjfiles= CONCAT('ALTER TABLE ',aa,' ADD COLUMN `staffSecretLevel` tinyint(1) DEFAULT 0 AFTER `staffNo`;');
      PREPARE stmt FROM @alertobjfiles;
      EXECUTE stmt;
      SET i=i+1;
    END WHILE;
  END $$
DELIMITER ;
CALL  update_enterprise_user_sub_table;


/* 在enterprise_user表中增加deptId 字段*/
DROP PROCEDURE IF EXISTS update_enterprise_user_sub_table;
DELIMITER $$
CREATE PROCEDURE update_enterprise_user_sub_table()
  SQL SECURITY INVOKER
  BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 0;
    WHILE i<100 DO
      SET aa=CONCAT('enterprise_user_',i);
      SET @alertobjfiles= CONCAT('ALTER TABLE ',aa,' ADD COLUMN `departmentId` bigint NOT NULL DEFAULT -1 AFTER `enterpriseId`;');
      PREPARE stmt FROM @alertobjfiles;
      EXECUTE stmt;
      SET i=i+1;
    END WHILE;
  END $$
DELIMITER ;
CALL  update_enterprise_user_sub_table;


/* 在user_account表中增加字段*/
DROP PROCEDURE IF EXISTS update_user_account_sub_table;
DELIMITER $$
CREATE PROCEDURE update_user_account_sub_table()
  SQL SECURITY INVOKER
  BEGIN
    DECLARE aa VARCHAR(256);
    DECLARE i INT DEFAULT 0;
    WHILE i<100 DO
      SET aa=CONCAT('user_account_',i);
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
CALL  update_user_account_sub_table;

