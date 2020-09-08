-- ----------------------------
-- Table structure for `user_profit_detail`
-- ----------------------------
DROP TABLE IF EXISTS `user_profit_detail`;
CREATE TABLE `user_profit_detail` (
  `id` varchar(32) NOT NULL,
  `userType` tinyint(2) NOT NULL,
  `cloudUserId` bigint(20) NOT NULL,
  `unionID` varchar(255) DEFAULT NULL,
  `openId` varchar(255) DEFAULT NULL,
  `orderId` varchar(255) NOT NULL,
  `enterpriseId` bigint(20) DEFAULT NULL,
  `userName` varchar(255) DEFAULT NULL,
  `payMoney` decimal(20,2) NOT NULL,
  `createAt` datetime DEFAULT NULL,
  `finishAt` datetime DEFAULT NULL,
  `status` tinyint(2) DEFAULT NULL,
  `type` tinyint(2) DEFAULT NULL,
  `proportions` double(20,4) DEFAULT NULL,
  `attempts` tinyint(20) DEFAULT NULL,
  `failReason` varchar(255) DEFAULT NULL,
  `source` varchar(255) DEFAULT NULL,
  `siteId` varchar(255) DEFAULT NULL,
  `appId` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `orderId` (`orderId`) USING BTREE,
  KEY `type_status` (`status`,`type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of user_profit_detail
-- ----------------------------
INSERT INTO `system_job_def` VALUES ('userProfitDetailTask', 'uam', 'userProfitDetailTask', '1', 'userProfitDetailTask', null, '1', '0 */5 * * * ?', '100', '1', '-1', '-1', '1', null);

ALTER TABLE `department_account`  ADD UNIQUE INDEX ` enterpriseId_deptId` (`enterpriseId`, `deptId`) USING BTREE ;
