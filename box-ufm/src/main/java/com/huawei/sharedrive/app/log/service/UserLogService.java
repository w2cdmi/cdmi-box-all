/**
 * 
 */
package com.huawei.sharedrive.app.log.service;

import java.util.List;

import com.huawei.sharedrive.app.event.service.EventConsumer;
import com.huawei.sharedrive.app.openapi.domain.userlog.UserLogListReq;
import com.huawei.sharedrive.app.openapi.domain.userlog.UserLogListRsp;

import pw.cdmi.common.log.UserLog;

/**
 * @author s00108907
 * 
 */
public interface UserLogService extends EventConsumer
{
    /**
     * 事件日志清理
     */
    void dataClean(String remainDays);
    
    /**
     * 批量提交日志写入
     */
    void batchWriteItems(List<UserLog> infos);
    
    /**
     * 单条日志写入
     */
    void batchWriteItem(UserLog data);
    
    UserLogListRsp queryLogs(UserLogListReq req);
}
