package pw.cdmi.box.uam.log.service;

import pw.cdmi.box.uam.log.domain.UserLogListReq;
import pw.cdmi.box.uam.log.domain.UserLogListRsp;
import pw.cdmi.box.uam.log.domain.UserLogType;
import pw.cdmi.common.log.UserLog;

public interface UserLogService
{
    UserLogListRsp queryLogs(UserLogListReq req);
    
    void saveUserLog(UserLog userLog, UserLogType logType, String[] params);
    
    void saveFailLog(String loginName, String appId, String[] params, UserLogType logType);
    
    void saveFailLog(String appId, UserLogType logType);
    
}
