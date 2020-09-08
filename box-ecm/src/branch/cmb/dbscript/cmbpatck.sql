use uam;
insert into `system_job_def` (`name`,`model`,`description`,`state`,`beanname`,`parameter`,`type`,`cron`,`recordnumber`,`threadnumber`,`datawait`,`clusterjobwait`,`pauseable`,`changeables`) 
    values ( 'syncCMBUserTask','uam','syncCMBUserTask',1,'syncOAManagerImpl','',0,'0 0 2 * * ?','100','1',-1,-1,'1','');
insert into `system_job_runtimeinfo` (`jobName`,`model`,`totalSuccess`,`totalFailed`,`lastResult`) 
	values ('syncCMBUserTask','uam','0','0',1);