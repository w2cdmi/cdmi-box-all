/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package com.huawei.sharedrive.uam.cluster.manage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.huawei.sharedrive.uam.weixin.service.WxUserService;
import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;

@Service("wxUserCleanInvitJob")
public class WxUserCleanInvitJob extends QuartzJobTask
{
    private static final Logger LOGGER = LoggerFactory.getLogger(WxUserCleanInvitJob.class);
    
    @Autowired
	private WxUserService wxUserService;
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
    	LOGGER.info("Start wxUserCleanInvit job");
    	try {
    		wxUserService.cleanCountTodayInvitByMe();
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error("wxUserCleanInvit fail {}",e.getMessage());
		}
    	
    }
    
}
