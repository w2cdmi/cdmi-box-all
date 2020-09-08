package com.huawei.sharedrive.isystem.syslog.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.adminlog.domain.QueryCondition;
import com.huawei.sharedrive.isystem.syslog.dao.UserLogDao;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;
import com.huawei.sharedrive.isystem.user.domain.Admin;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.common.log.UserLog;
import pw.cdmi.core.utils.IpUtils;

@Service("userLogService")
public class UserLogServiceImpl implements UserLogService
{
    public static final Logger LOGGER = LoggerFactory.getLogger(UserLogServiceImpl.class);
    
    @Autowired
    private UserLogDao userLogDao;
    
    @Override
    public String saveFailLog(String loginName, String appId, String[] params, UserLogType logType)
    {
        boolean isContinue = unsaveLog(logType);
        if (!isContinue)
        {
            return null;
        }
        UserLog userLog = new UserLog();
        userLog.setAppId(appId);
        if (loginName.length() > 255)
        {
            userLog.setKeyword(loginName);
        }
        else
        {
            userLog.setLoginName(loginName);
        }
        userLog.setDetail(logType.getDetails(params));
        userLog.setType(logType.getValue());
        return userLogDao.insert(userLog);
    }
    
    @Override
    public String saveUserLog(UserLog userLog)
    {
        if (userLog == null)
        {
            LOGGER.info("Fail to create log userLog is null");
            return null;
        }
        return userLogDao.insert(userLog);
    }
    
    @Override
    public String saveUserLog(UserLog userLog, UserLogType logType, String[] params)
    {
        boolean isContinue = unsaveLog(logType);
        if (!isContinue)
        {
            return null;
        }
        userLog.setDetail(logType.getDetails(params));
        userLog.setType(logType.getValue());
        return saveUserLog(userLog);
    }
    
    private boolean unsaveLog(UserLogType logType)
    {
        if (logType == null || !logType.isEnable())
        {
            return false;
        }
        return true;
    }
    
    @Override
    public UserLog initUserLog(HttpServletRequest request, UserLogType logType, String[] params)
    {
        
        UserLog userLog = new UserLog();
        userLog.setId(UUID.randomUUID().toString());
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        if (null != admin)
        {
            userLog.setLoginName(admin.getLoginName());
            userLog.setUserId(admin.getId());
        }
        userLog.setCreatedAt(new Date());
        userLog.setClientAddress(IpUtils.getClientAddress(request));
        userLog.setDetail(logType.getErrorDetails(params));
        userLog.setType(logType.getValue());
        userLog.setKeyword(logType.getType(null));
        userLog.setLevel(FIAL_LEVEL);
        return userLog;
    }
    
    @Override
    public void update(UserLog userLog)
    {
        userLogDao.updateUserLog(userLog);
        
    }
    
    @Override
    public Page<UserLog> queryPage(QueryCondition condition)
    {
        
        int total = userLogDao.getTotals(condition);
        List<UserLog> content = userLogDao.getListUserLog(condition);
        setType(content);
        Page<UserLog> page = new PageImpl<UserLog>(content, condition.getPageRequest(), total);
        return page;
    }
    
    private void setType(List<UserLog> content)
    {
        UserLogType type;
        for (UserLog userLog : content)
        {
            type = UserLogType.build(userLog.getType());
            if (type != null)
            {
                userLog.setId(type.getType(null));
                if (userLog.getLevel() == FIAL_LEVEL)
                {
                    userLog.setAppId(type.getWrongRank(null));
                }
                else if (userLog.getLevel() == SUCCESS_LEVEL)
                {
                    userLog.setAppId(type.getRightRank(null));
                }
            }
        }
    }
}
