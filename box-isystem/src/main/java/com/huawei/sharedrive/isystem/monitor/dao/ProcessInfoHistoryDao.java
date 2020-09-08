package com.huawei.sharedrive.isystem.monitor.dao;

import com.huawei.sharedrive.isystem.monitor.domain.ProcessInfo;

/**进程监控
 * @author l00357199  
 * 20162016-1-5 下午8:22:57 
 */
public interface ProcessInfoHistoryDao
{
    void insert(ProcessInfo processInfo);

}
