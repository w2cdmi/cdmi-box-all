package com.huawei.sharedrive.uam.user.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.enterpriseuser.service.UserLdapService;
import com.huawei.sharedrive.uam.user.shiro.MemCacheShiroSessionDAO;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;

@Component
public class DeleteLdapTempUser extends QuartzJobTask
{
    private static final long serialVersionUID = -1462988860338743555L;
    
    private final static Logger LOGGER = LoggerFactory.getLogger(DeleteLdapTempUser.class);
    
    @Autowired
    private MemCacheShiroSessionDAO memCacheShiroSessionDAO;
    
    @Autowired
    private UserLdapService userLdapService;
    
    @Override
    public void doTask(JobExecuteContext arg0, JobExecuteRecord arg1)
    {
        deleteLdapTempUser();
    }
    
    /**
     * 
     * @param authMsg
     * @param sessionId
     * @return
     */
    private void deleteLdapTempUser()
    {
        LOGGER.info("delete temp user begin");
        List<String> sessionList = userLdapService.getSessionList();
        if (CollectionUtils.isEmpty(sessionList))
        {
            LOGGER.info("delete temp user end,sessionList is null");
            return;
        }
        Session session;
        List<String> destroyedSessionList = new ArrayList<String>(10);
        for (String sessionId : sessionList)
        {
            try
            {
                session = memCacheShiroSessionDAO.readSession(sessionId);
            }
            catch (UnknownSessionException e)
            {
                LOGGER.error("Fail to read get the deleted sessionId ");
                session = null;
            }
            if (null == session)
            {
                destroyedSessionList.add(sessionId);
            }
        }
        if (destroyedSessionList.size() < 1)
        {
            LOGGER.info("delete temp user end,destroyedSessionList size is " + destroyedSessionList.size());
            return;
        }
        for (String id : destroyedSessionList)
        {
            userLdapService.deleteBySessionId(id);
        }
        LOGGER.info("delete temp user end");
    }
    
}
