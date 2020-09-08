use ds_sysdb;
delete from system_job_def where name='reportDCStatusJob';
delete from system_job_runtimeinfo where jobName='reportDCStatusJob';
delete from system_job_def where name='licenseRetryJob';
delete from system_job_runtimeinfo where jobName='licenseRetryJob';
insert into `system_config` values ('system.datacenter.type','merge','system.datacenter.type',1,1);
insert into `system_config` values ('system.sk.cache.timeout','86400000','system.sk.cache.timeout',1,1);
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) 
    values ('clearSKCacheJob','dss','clearSKCacheJob',1,'clearSKCacheJob','',0,'0 0/30 * * * ?',100,1,-1,-1,0,'');
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) values ('clearSKCacheJob','dss',0,0,1);

ALTER TABLE fs_endpoint_baseinfo MODIFY COLUMN `endpoint` varchar(4096);

update system_job_def set pauseable=0 where name='concurrentCheckTask' and model='dss';
update system_job_def set pauseable=0 where name='refreshStorageResourceJob' and model='dss';
update system_job_def set pauseable=0 where name='reportConcStatusJob' and model='dss';
update system_job_def set pauseable=0 where name='mergerMultipartJob' and model='dss';
update system_job_def set pauseable=0 where name='checkSelfStatusJob' and model='dss';