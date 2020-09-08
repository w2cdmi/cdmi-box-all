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
package com.huawei.sharedrive.isystem.logfile.dao;

import java.util.List;

import com.huawei.sharedrive.isystem.logfile.domain.LogAgent;

/**
 * 
 * @author s90006125
 *
 */
public interface LogAgentDao
{
    LogAgent select(int id);
    
    LogAgent selectByClusterId(int clusterId);
    
    List<LogAgent> selectAll();
}
