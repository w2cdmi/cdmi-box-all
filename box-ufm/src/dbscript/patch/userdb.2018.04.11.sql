

DROP PROCEDURE IF EXISTS update_inode_link_isProgram_table;
DELIMITER $$
CREATE PROCEDURE update_inode_link_isProgram_table()
  SQL SECURITY INVOKER
  BEGIN
    DECLARE i INT DEFAULT 0;
    WHILE i<100 DO
      SET @sql1= CONCAT('ALTER TABLE inode_link_', i, ' ADD COLUMN `disdump` tinyint(4) DEFAULT NULL;');
      SET @sql2= CONCAT('ALTER TABLE inode_link_', i, ' ADD COLUMN `isProgram` tinyint(4) DEFAULT NULL;');
      
      SET @sql4= CONCAT('ALTER TABLE inode_link_r_', i, ' ADD COLUMN `disdump` tinyint(255) DEFAULT NULL;');
      SET @sql5= CONCAT('ALTER TABLE inode_link_r_', i, ' ADD COLUMN `isProgram` tinyint(255) DEFAULT NULL;');
      PREPARE stmt1 FROM @sql1;
      PREPARE stmt2 FROM @sql2;
      PREPARE stmt4 FROM @sql4;
      PREPARE stmt5 FROM @sql5;
      EXECUTE stmt1;
      EXECUTE stmt2;
      EXECUTE stmt4;
      EXECUTE stmt5;
      SET i = i + 1;
    END WHILE;
  END $$
DELIMITER ;
CALL  update_inode_link_isProgram_table;