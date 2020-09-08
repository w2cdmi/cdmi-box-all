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
package com.huawei.sharedrive.app.user.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.user.dao.UserDAOV2;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.service.UserService;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;

/**
 * 
 * @author s90006125
 * 
 */
@Service("clearUserTask")
public class ClearUserTask extends QuartzJobTask
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ClearUserTask.class);
    
    @Autowired
    private UserDAOV2 userDAO;
    
    @Autowired
    private UserService userService;
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        List<User> list = userDAO.getUserByStatus(User.USER_DELETING);
        if (CollectionUtils.isEmpty(list))
        {
            return;
        }
        
        List<Long> failedList = new ArrayList<Long>(1);
        for (User user : list)
        {
            try
            {
                userService.deleteUser(user.getAccountId(), user.getId());
            }
            catch (Exception e)
            {
                LOGGER.error("del user failed userid:" + user.getId());
                failedList.add(user.getId());
            }
        }
        
        if (failedList.isEmpty())
        {
            return;
        }
        
        StringBuilder sb = new StringBuilder("del user failed. [ ");
        for (Long userid : failedList)
        {
            sb.append(userid).append(',');
        }
        sb.append(" ]");
        record.setSuccess(false);
        record.setOutput(sb.toString());
    }
    
}
