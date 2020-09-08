package com.huawei.sharedrive.uam.log.service;

import com.huawei.sharedrive.uam.log.domain.UserLogListReq;
import com.huawei.sharedrive.uam.log.domain.UserLogListRsp;
import com.huawei.sharedrive.uam.log.domain.UserLogType;

import pw.cdmi.common.log.UserLog;

public interface UserLogService
{
    UserLogListRsp queryLogs(UserLogListReq req);
    
    void saveUserLog(UserLog userLog, UserLogType logType, String[] params);
    
    void saveFailLog(String loginName, String appId, String[] params, UserLogType logType);
    
    void saveFailLog(UserLog userLog, String loginName, String appId, String[] params, UserLogType logType);
    
    void saveFailLog(UserLog userLog, UserLogType logType, String[] params);
    
}
