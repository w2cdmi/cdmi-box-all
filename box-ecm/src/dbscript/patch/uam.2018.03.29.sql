use uam;

-- ----------------------------
-- Table structure for `product`
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `type` tinyint(4) NOT NULL,
  `accountNum` bigint(20) NOT NULL,
  `accountSpace` bigint(20) NOT NULL,
  `teamNum` bigint(20) NOT NULL,
  `teamSpace` bigint(20) NOT NULL,
  `introduce` varchar(255) NOT NULL,
  `companyName` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES ('1', '企业高级版', '2', '40', '2147483648', '40', '10737418240', '企业高级版', '华一云网');
INSERT INTO `product` VALUES ('2', '企业专业版', '2', '80', '10737418240', '80', '53687091200', '企业专业版', '华一云网');
INSERT INTO `product` VALUES ('3', '企业旗舰版', '2', '200', '53687091200', '200', '107374182400', '企业旗舰版', '华一云网');
INSERT INTO `product` VALUES ('4', '黄金VIP', '1', '1', '5368709120', '0', '0', '扩展容量5G', '华一云网');
INSERT INTO `product` VALUES ('5', '黄金VIP', '1', '1', '10737418240', '0', '0', '扩展容量10G', '华一云网');
INSERT INTO `product` VALUES ('6', '铂金VIP', '1', '1', '21474836480', '0', '0', '扩展容量20G', '华一云网');
INSERT INTO `product` VALUES ('7', '铂金VIP', '1', '1', '51539607552', '0', '0', '扩展容量48G', '华一云网');
INSERT INTO `product` VALUES ('8', '钻石VIP', '1', '1', '94489280512', '0', '0', '扩展容量88G', '华一云网');

-- ----------------------------
-- Table structure for `product_duration_price`
-- ----------------------------
DROP TABLE IF EXISTS `product_duration_price`;
CREATE TABLE `product_duration_price` (
  `id` tinyint(4) NOT NULL,
  `productId` bigint(20) DEFAULT NULL,
  `duration` tinyint(4) DEFAULT NULL,
  `price` float(20,0) DEFAULT NULL,
  `createDate` datetime DEFAULT NULL,
  `updateDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of product_duration_price
-- ----------------------------
INSERT INTO `product_duration_price` VALUES ('1', '4', '3', '650', '2018-03-20 16:01:04', '2018-03-20 16:01:07');
INSERT INTO `product_duration_price` VALUES ('2', '4', '12', '2000', '2018-03-20 16:01:56', '2018-03-20 16:01:59');
INSERT INTO `product_duration_price` VALUES ('3', '4', '24', '4000', '2018-03-20 16:02:30', '2018-03-20 16:02:33');
INSERT INTO `product_duration_price` VALUES ('4', '4', '36', '6000', '2018-03-20 16:03:03', '2018-03-20 16:03:06');
INSERT INTO `product_duration_price` VALUES ('5', '5', '12', '3800', '2018-03-20 16:03:55', '2018-03-20 16:03:58');
INSERT INTO `product_duration_price` VALUES ('6', '5', '24', '7600', '2018-03-20 16:04:23', '2018-03-20 16:04:26');
INSERT INTO `product_duration_price` VALUES ('7', '5', '36', '11400', '2018-03-20 16:04:38', '2018-03-20 16:04:40');
INSERT INTO `product_duration_price` VALUES ('8', '6', '12', '5800', '2018-03-20 16:05:09', '2018-03-20 16:05:11');
INSERT INTO `product_duration_price` VALUES ('9', '6', '24', '11600', '2018-03-20 16:05:28', '2018-03-20 16:05:30');
INSERT INTO `product_duration_price` VALUES ('10', '6', '36', '17400', '2018-03-20 16:08:56', '2018-03-20 16:08:58');
INSERT INTO `product_duration_price` VALUES ('11', '7', '12', '9900', '2018-03-20 16:09:45', '2018-03-20 16:09:47');
INSERT INTO `product_duration_price` VALUES ('12', '7', '24', '19800', '2018-03-20 16:10:08', '2018-03-20 16:10:10');
INSERT INTO `product_duration_price` VALUES ('13', '7', '36', '29800', '2018-03-20 16:10:30', '2018-03-20 16:10:33');
INSERT INTO `product_duration_price` VALUES ('14', '8', '12', '16800', '2018-03-20 16:11:01', '2018-03-20 16:11:04');
INSERT INTO `product_duration_price` VALUES ('15', '8', '24', '33600', '2018-03-20 16:11:24', '2018-03-20 16:11:27');
INSERT INTO `product_duration_price` VALUES ('16', '8', '36', '50400', '2018-03-20 16:11:48', '2018-03-20 16:11:51');



-- ----------------------------
-- Table structure for `product_rebate`
-- ----------------------------
DROP TABLE IF EXISTS `product_rebate`;
CREATE TABLE `product_rebate` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `productId` bigint(20) NOT NULL,
  `duration` bigint(20) DEFAULT NULL,
  `DiscountRatio` float(6,2) NOT NULL,
  `startDate` date DEFAULT NULL,
  `endDate` date DEFAULT NULL,
  `introduce` varchar(255) DEFAULT NULL,
  `createDate` datetime DEFAULT NULL,
  `updateDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of product_rebate
-- ----------------------------
INSERT INTO `product_rebate` VALUES ('1', '4', '24', '0.90', '2018-01-13', '2018-01-13', '123', '2018-01-16 00:00:00', '2018-01-18 00:00:00');
INSERT INTO `product_rebate` VALUES ('2', '4', '36', '0.80', '2018-01-13', '2018-01-13', '234', '2018-01-13 16:55:11', '2018-01-13 16:55:14');
INSERT INTO `product_rebate` VALUES ('3', '5', '24', '0.90', '2018-01-13', '2018-01-13', '235', '2018-01-13 16:55:44', '2018-01-13 16:55:46');
INSERT INTO `product_rebate` VALUES ('4', '5', '36', '0.80', '2018-01-13', '2018-01-13', '123', '2018-01-13 16:56:23', '2018-01-13 16:56:26');
INSERT INTO `product_rebate` VALUES ('5', '6', '24', '0.90', '2018-01-13', '2018-01-13', '235', '2018-01-13 16:56:59', '2018-01-13 16:57:01');
INSERT INTO `product_rebate` VALUES ('6', '6', '36', '0.80', '2018-01-13', '2018-01-13', '235', '2018-01-13 16:57:34', '2018-01-13 16:57:37');
INSERT INTO `product_rebate` VALUES ('13', '7', '24', '0.90', '2018-03-20', '2018-03-20', '33', '2018-03-20 16:24:22', '2018-03-20 16:24:24');
INSERT INTO `product_rebate` VALUES ('14', '7', '36', '0.80', '2018-03-20', '2018-03-20', '55', '2018-03-20 16:24:55', '2018-03-20 16:24:57');
INSERT INTO `product_rebate` VALUES ('15', '8', '24', '0.90', '2018-03-20', '2018-03-20', '55', '2018-03-20 16:25:26', '2018-03-20 16:25:29');
INSERT INTO `product_rebate` VALUES ('16', '8', '36', '0.80', '2018-03-20', '2018-03-20', '55', '2018-03-20 16:25:50', '2018-03-20 16:25:52');
INSERT INTO `product_rebate` VALUES ('20', '1', '12', '0.90', '2018-01-17', '2018-01-17', '11', '2018-01-17 19:39:40', '2018-01-17 19:39:42');
INSERT INTO `product_rebate` VALUES ('21', '1', '24', '0.80', '2018-01-17', '2018-01-17', '11', '2018-01-17 19:40:05', '2018-01-17 19:40:08');
INSERT INTO `product_rebate` VALUES ('22', '2', '12', '0.90', '2018-01-17', '2018-01-17', '11', '2018-01-17 19:40:33', '2018-01-17 19:40:35');
INSERT INTO `product_rebate` VALUES ('23', '2', '24', '0.80', '2018-01-17', '2018-01-17', '11', '2018-01-17 19:41:20', '2018-01-17 19:41:22');
INSERT INTO `product_rebate` VALUES ('24', '3', '12', '0.90', '2018-01-17', '2018-01-17', '22', '2018-01-17 19:41:44', '2018-01-17 19:41:47');
INSERT INTO `product_rebate` VALUES ('25', '3', '24', '0.80', '2018-01-17', '2018-01-17', '11', '2018-01-17 19:42:22', '2018-01-17 19:42:24');

ALTER TABLE order_bill DROP COLUMN price;