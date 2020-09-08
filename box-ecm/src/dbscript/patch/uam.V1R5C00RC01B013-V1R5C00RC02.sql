use uam;
ALTER TABLE `access_config` ADD `spaceRoleId` bigint(20) DEFAULT -1;
ALTER TABLE `access_config` DROP INDEX `access_config_union_index`;
ALTER TABLE `access_config` ADD UNIQUE INDEX `access_config_union_index` (`accountId`,`safeRoleId`,`spaceRoleId`, `netRegionId`,`clientType`) ;
