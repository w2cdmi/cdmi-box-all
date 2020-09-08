package pw.cdmi.box.uam.log.dao;

import java.util.Date;
import java.util.List;

import pw.cdmi.box.uam.log.domain.UserLogListReq;
import pw.cdmi.common.log.UserLog;

public interface UserLoginLogDAO
{
    
    String EVENT_LOG_DATE_PATTERN = "yyyyMMdd";
    
    void create(UserLog userLoginLog);
    
    void createTable(Date ltDate);
    
    void dropTable(Date ltDate);
    
    long getTotals(UserLogListReq req, String tableName);
    
    List<UserLog> getUserLoginLogList(UserLogListReq req, String tableName, long offset, int limit);
}
