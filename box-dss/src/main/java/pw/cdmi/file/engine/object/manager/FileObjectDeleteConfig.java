/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.manager;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 * 
 * @author s90006125
 *
 */
public class FileObjectDeleteConfig
{
    //{"tableCount":10,"time":100,"retryTimes":10,"reserveTime":30,"maxTimeConsuming":3000000, "limit":10}
    
    /**
     * fileobject_delete_task的数量，默认为每个库10张表，0~9
     */
    private int tableCount = 10;
    
    /**
     * 删除失败后的重试超时时间，单位为：分钟；
     */
    private int timeout;
    
    /**
     * 删除失败后的重试次数，最新为0，表示只删除一次
     */
    private int retryTimes;
    
    /**
     * 保留时间，即收到删除命令后，需保留多长时间后再执行彻底删除，未防止误删除的最后规避手段，单位为：天
     */
    private int reserveTime;
    
    /**
     * 最大耗时，每次执行删除操作，最大耗时时间，如果时间太长，则退出本来删除，等待下一次定时任务执行，单位为：毫秒
     */
    private long maxTimeConsuming;

    /**
     * 每次从一张表中获取的最大数量
     */
    private int limit;
    
    /**
     * 每次删除一轮数据后的等待时间，单位毫秒，默认为1000毫秒，为了规避存储删除压力过大的问题
     */
    private long deleteWait;
    
    public int getTableCount()
    {
        return tableCount;
    }

    public void setTableCount(int tableCount)
    {
        this.tableCount = tableCount;
    }

    public int getTimeout()
    {
        return timeout;
    }

    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }

    public int getRetryTimes()
    {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes)
    {
        this.retryTimes = retryTimes;
    }

    public int getReserveTime()
    {
        return reserveTime;
    }

    public void setReserveTime(int reserveTime)
    {
        this.reserveTime = reserveTime;
    }

    public long getMaxTimeConsuming()
    {
        return maxTimeConsuming;
    }

    public void setMaxTimeConsuming(long maxTimeConsuming)
    {
        this.maxTimeConsuming = maxTimeConsuming;
    }

    public int getLimit()
    {
        return limit;
    }

    public void setLimit(int limit)
    {
        this.limit = limit;
    }
    
    public long getDeleteWait()
    {
        return deleteWait;
    }

    public void setDeleteWait(long deleteWait)
    {
        this.deleteWait = deleteWait;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }
}
