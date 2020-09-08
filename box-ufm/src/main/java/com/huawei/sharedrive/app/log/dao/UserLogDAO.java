/**
 * 
 */
package com.huawei.sharedrive.app.log.dao;

import java.util.Date;
import java.util.List;

import com.huawei.sharedrive.app.openapi.domain.userlog.UserLogListReq;

import pw.cdmi.common.log.UserLog;

public interface UserLogDAO
{
    
    String EVENT_LOG_DATE_PATTERN = "yyyyMMdd";
    
    /**
     * 创建对象
     * 
     * @param userLog
     */
    void create(UserLog userLog);
    
    /**
     * 创建指定时间的表,表名like "user_log_yyyyMMdd
     * 
     * @param ltDate
     */
    void createTable(Date ltDate);
    
    /**
     * 删除指定时间的表,表名like "user_log_yyyyMMdd
     * 
     * @param ltDate
     */
    void dropTable(Date ltDate);
    
    long getTotals(UserLogListReq req, String tableName);
    
    List<UserLog> getUserLogList(UserLogListReq req, String tableName, long offset, int limit);
    
}
