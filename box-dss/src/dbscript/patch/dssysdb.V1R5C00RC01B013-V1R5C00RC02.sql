use ds_sysdb;
update system_job_def set pauseable=0 where name='concurrentCheckTask' and model='dss';
update system_job_def set pauseable=0 where name='refreshStorageResourceJob' and model='dss';
update system_job_def set pauseable=0 where name='reportConcStatusJob' and model='dss';
update system_job_def set pauseable=0 where name='mergerMultipartJob' and model='dss';
update system_job_def set pauseable=0 where name='checkSelfStatusJob' and model='dss';