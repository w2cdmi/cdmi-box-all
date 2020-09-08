/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package com.huawei.sharedrive.isystem.logfile.service;

import java.io.IOException;

import com.huawei.sharedrive.isystem.logfile.domain.QueryCondition;
import com.huawei.sharedrive.isystem.logfile.domain.QueryResult;

/**
 * 
 * @author s90006125
 *
 */
public interface LogFileService
{
    /**
     * 查询指定集群的日志文件
     * @param clusterId
     * @param condition
     * @return
     */
    QueryResult searchFile(int clusterId, QueryCondition condition);
    
    /**
     * 下载指定的日志文件
     * @param clusterId  集群ID
     * @param id              文件ID
     * @return
     */
    DownloadFileResponse downLoadLogFile(int clusterId, String id) throws IOException;
}
