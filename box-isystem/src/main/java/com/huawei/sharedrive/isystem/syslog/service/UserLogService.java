package com.huawei.sharedrive.isystem.syslog.service;

import javax.servlet.http.HttpServletRequest;

import com.huawei.sharedrive.isystem.adminlog.domain.QueryCondition;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;

import pw.cdmi.box.domain.Page;
import pw.cdmi.common.log.UserLog;

public interface UserLogService
{
    byte FIAL_LEVEL = (byte) 1;
    
    byte SUCCESS_LEVEL = (byte) 0;
    
    String saveFailLog(String loginName, String appId, String[] params, UserLogType logType);
    
    String saveUserLog(UserLog userLog, UserLogType logType, String[] params);
    
    void update(UserLog userLog);
    
    UserLog initUserLog(HttpServletRequest request, UserLogType logType, String[] params);
    
    String saveUserLog(UserLog userLog);
    
    Page<UserLog> queryPage(QueryCondition condition);
    
}
