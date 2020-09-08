USE `userdb_0`;

/* 在inode-share表中增加字段*/
DROP PROCEDURE IF EXISTS update_inode_share_sub_table;
DELIMITER $$
CREATE PROCEDURE update_inode_share_sub_table()
  SQL SECURITY INVOKER
  BEGIN
    DECLARE i INT DEFAULT 0;
    WHILE i<100 DO
      SET @sql1= CONCAT('ALTER TABLE inode_share_', i, ' ADD COLUMN `originalType` smallint(3) DEFAULT NULL;');
      SET @sql2= CONCAT('ALTER TABLE inode_share_', i, ' ADD COLUMN `originalNodeId` bigint(20) DEFAULT NULL;');
      SET @sql3= CONCAT('ALTER TABLE inode_share_', i, ' ADD COLUMN `originalOwnerId` bigint(20) DEFAULT NULL;');
      SET @sql4= CONCAT('ALTER TABLE inode_share_r_', i, ' ADD COLUMN `originalType` smallint(3) DEFAULT NULL;');
      SET @sql5= CONCAT('ALTER TABLE inode_share_r_', i, ' ADD COLUMN `originalNodeId` bigint(20) DEFAULT NULL;');
      SET @sql6= CONCAT('ALTER TABLE inode_share_r_', i, ' ADD COLUMN `originalOwnerId` bigint(20) DEFAULT NULL;');
      PREPARE stmt1 FROM @sql1;
      PREPARE stmt2 FROM @sql2;
      PREPARE stmt3 FROM @sql3;
      PREPARE stmt4 FROM @sql4;
      PREPARE stmt5 FROM @sql5;
      PREPARE stmt6 FROM @sql6;
      EXECUTE stmt1;
      EXECUTE stmt2;
      EXECUTE stmt3;
      EXECUTE stmt4;
      EXECUTE stmt5;
      EXECUTE stmt6;
      SET i = i + 1;
    END WHILE;
  END $$
DELIMITER ;
CALL  update_inode_share_sub_table;



/* 在inode-share表中增加字段*/
DROP PROCEDURE IF EXISTS update_inode_share_shareType_table;
DELIMITER $$
CREATE PROCEDURE update_inode_share_shareType_table()
  SQL SECURITY INVOKER
  BEGIN
    DECLARE i INT DEFAULT 0;
    WHILE i<100 DO
      SET @sql1= CONCAT('ALTER TABLE inode_share_', i, ' ADD COLUMN `shareType` varchar(255) DEFAULT NULL;');
      SET @sql2= CONCAT('ALTER TABLE inode_share_', i, ' ADD COLUMN `linkCode` varchar(255) DEFAULT NULL;');
      SET @sql3= CONCAT('ALTER TABLE inode_share_', i, ' ADD COLUMN `forwardId` bigint(20) DEFAULT NULL;');
      
      SET @sql4= CONCAT('ALTER TABLE inode_share_r_', i, ' ADD COLUMN `shareType` varchar(255) DEFAULT NULL;');
      SET @sql5= CONCAT('ALTER TABLE inode_share_r_', i, ' ADD COLUMN `linkCode` varchar(255) DEFAULT NULL;');
      SET @sql6= CONCAT('ALTER TABLE inode_share_r_', i, ' ADD COLUMN `forwardId` bigint(20) DEFAULT NULL;');
      PREPARE stmt1 FROM @sql1;
      PREPARE stmt2 FROM @sql2;
      PREPARE stmt3 FROM @sql3;
      PREPARE stmt4 FROM @sql4;
      PREPARE stmt5 FROM @sql5;
      PREPARE stmt6 FROM @sql6;
      EXECUTE stmt1;
      EXECUTE stmt2;
      EXECUTE stmt3;
      EXECUTE stmt4;
      EXECUTE stmt5;
      EXECUTE stmt6;
      SET i = i + 1;
    END WHILE;
  END $$
DELIMITER ;
CALL  update_inode_share_shareType_table;



DROP PROCEDURE IF EXISTS update_inode_link_sub_table;
DELIMITER $$
CREATE PROCEDURE update_inode_link_sub_table()
  SQL SECURITY INVOKER
  BEGIN
    DECLARE i INT DEFAULT 0;
    WHILE i<100 DO
      SET @sql1= CONCAT('ALTER TABLE inode_link_', i, ' ADD COLUMN `subINodes` varchar(255) DEFAULT NULL;');
      SET @sql2= CONCAT('ALTER TABLE inode_link_', i, ' ADD COLUMN `needLogin` tinyint(4) DEFAULT NULL;');
      SET @sql3= CONCAT('ALTER TABLE inode_link_r_', i, ' ADD COLUMN `subINodes` varchar(255) DEFAULT NULL;');
      SET @sql4= CONCAT('ALTER TABLE inode_link_r_', i, ' ADD COLUMN `needLogin` tinyint(4) DEFAULT NULL;');
      PREPARE stmt1 FROM @sql1;
      PREPARE stmt2 FROM @sql2;
      PREPARE stmt3 FROM @sql3;
      PREPARE stmt4 FROM @sql4;
      EXECUTE stmt1;
      EXECUTE stmt2;
      EXECUTE stmt3;
      EXECUTE stmt4;
      SET i = i + 1;
    END WHILE;
  END $$
DELIMITER ;
CALL  update_inode_link_sub_table;


DROP PROCEDURE IF EXISTS update_inode_link_needLogin_table;
DELIMITER $$
CREATE PROCEDURE update_inode_link_needLogin_table()
  SQL SECURITY INVOKER
  BEGIN
    DECLARE i INT DEFAULT 0;
    WHILE i<100 DO
      SET @sql1= CONCAT('UPDATE  inode_link_', i, ' set needLogin=0');
      SET @sql4= CONCAT('UPDATE  inode_link_r_', i, ' set needLogin=0');
      PREPARE stmt1 FROM @sql1;
     
      PREPARE stmt4 FROM @sql4;
  
      EXECUTE stmt1;

      EXECUTE stmt4;

      SET i = i + 1;
    END WHILE;
  END $$
DELIMITER ;
CALL  update_inode_link_needLogin_table;