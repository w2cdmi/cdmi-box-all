ALTER TABLE `wx_user` ADD COLUMN `countTotalProfits`  bigint(20) NULL AFTER `shareLevel`;
ALTER TABLE `wx_user` ADD COLUMN `countTodayProfits`  bigint(20) NULL AFTER `countTotalProfits`;
update wx_user set countTotalProfits=0;
update wx_user set countTodayProfits=0;