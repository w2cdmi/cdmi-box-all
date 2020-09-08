use uam;
ALTER TABLE enterprise ADD `pwdLevel` varchar(20) DEFAULT NULL;


ALTER TABLE `department_account`
DROP COLUMN `fileNeedApprove`,
ADD COLUMN `fileNeedApprove`  tinyint(4) NULL AFTER `downloadBandWidth`;