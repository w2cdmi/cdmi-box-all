package com.huawei.sharedrive.app.filelabel.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.filelabel.service.IFileLabelService;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;

/**
 * 
 * Desc  : 标签定时清除绑定次数不大于0的标签信息
 * Author: 77235
 * Date	 : 2016年12月7日
 */
@Component("clearFilelabelJob")
public class ClearFilelabelJob extends QuartzJobTask {
	 private static final Logger LOGGER = LoggerFactory.getLogger(ClearFilelabelJob.class);
	
	@Autowired
	private IFileLabelService fileLabelService;
	
	@Override
	public void doTask(JobExecuteContext context, JobExecuteRecord executeRecord) {
		LOGGER.info("[ClearFilelabelJob] begin execute clear filelabel job task...");
		
		fileLabelService.clearFilelabelsWithBindtimesLessThanOne();
		
		LOGGER.info("[ClearFilelabelJob] end execute clear filelabel job task...");
	}
}
