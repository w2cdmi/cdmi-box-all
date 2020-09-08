DROP TABLE IF EXISTS `share_level`;
CREATE TABLE `share_level` (
  `id` int(20) NOT NULL,
  `startRange` bigint(20) DEFAULT NULL,
  `endRange` bigint(20) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `iconUrl` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `proportions` float(20,4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of share_level
-- ----------------------------
INSERT INTO `share_level` VALUES ('1', '0', '10', '青铜会员 ', null, '青铜会员', '0.0300');
INSERT INTO `share_level` VALUES ('2', '10', '30', '白银会员', null, '白银会员', '0.0400');
INSERT INTO `share_level` VALUES ('3', '30', '100', '黄金会员', null, '黄金会员', '0.0500');
INSERT INTO `share_level` VALUES ('4', '100', '500', '铂金会员', null, '铂金会员', '0.0600');
INSERT INTO `share_level` VALUES ('5', '500', '999999999', '王者会员', null, '王者会员', '0.0800');

ALTER TABLE `wx_user` ADD COLUMN `inviterId`  varchar(255) NULL AFTER `type`;
ALTER TABLE `wx_user` ADD COLUMN `countInvitByMe`  bigint(20) NULL AFTER `inviterId`;
ALTER TABLE `wx_user` ADD COLUMN `countTodayInvitByMe`  bigint(20) NULL AFTER `countInvitByMe`;
ALTER TABLE `wx_user` ADD COLUMN `shareLevel`  tinyint(4) NULL AFTER `countTodayInvitByMe`;

UPDATE `wx_user` SET `countInvitByMe`=0;
UPDATE `wx_user` SET `countTodayInvitByMe`=0;
UPDATE `wx_user` SET `shareLevel`=1;
INSERT INTO `system_job_def` VALUES ('wxUserCleanInvitJob', 'uam', 'wxUserCleanInvitJob', '1', 'wxUserCleanInvitJob', null, '1', '0 0 0 * * ?', '100', '1', '-1', '-1', '1', null);


