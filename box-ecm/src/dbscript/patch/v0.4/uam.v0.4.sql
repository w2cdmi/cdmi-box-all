DROP TABLE IF EXISTS `wxwork_corp_app`;
CREATE TABLE `wxwork_corp_app` (
  `corpId` varchar(32) NOT NULL COMMENT '企业id',
  `suiteId` varchar(32) NOT NULL COMMENT '应用套件id',
  `appId` int(11) NOT NULL COMMENT '服务商套件中的对应应用id',
  `agentId` int(11) NOT NULL COMMENT '授权方应用id',
  `name` varchar(64) DEFAULT NULL,
  `roundLogoUrl` varchar(128) DEFAULT NULL,
  `squareLogoUrl` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`corpId`,`suiteId`,`appId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
