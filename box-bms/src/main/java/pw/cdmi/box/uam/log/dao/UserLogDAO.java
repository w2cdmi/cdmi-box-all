package pw.cdmi.box.uam.log.dao;

import java.util.Date;
import java.util.List;

import pw.cdmi.box.uam.log.domain.UserLogListReq;
import pw.cdmi.common.log.UserLog;

public interface UserLogDAO
{
    
    String EVENT_LOG_DATE_PATTERN = "yyyyMMdd";
    
    /**
     * 
     * @param eventLog
     */
    void create(UserLog userLog);
    
    /**
     * 
     * @param ltDate
     */
    void createTable(Date ltDate);
    
    /**
     * 
     * @param ltDate
     */
    void dropTable(Date ltDate);
    
    long getTotals(UserLogListReq req, String tableName);
    
    List<UserLog> getUserLogList(UserLogListReq req, String tableName, long offset, int limit);
}
