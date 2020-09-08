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
package com.huawei.sharedrive.isystem.logfile.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author s90006125
 *
 */
public class QueryResult implements Serializable
{
    private static final long serialVersionUID = -6881354543120649076L;
    
    private QueryCondition condition;
    
    private List<LogFile> logFiles;
    
    private long total;

    public QueryCondition getCondition()
    {
        return condition;
    }

    public void setCondition(QueryCondition condition)
    {
        this.condition = condition;
    }

    public List<LogFile> getLogFiles()
    {
        return logFiles;
    }

    public void setLogFiles(List<LogFile> logFiles)
    {
        this.logFiles = logFiles;
    }

    public long getTotal()
    {
        return total;
    }

    public void setTotal(long total)
    {
        this.total = total;
    }
}
