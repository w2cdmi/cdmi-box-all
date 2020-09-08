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
public class MultipartFileObjectClearConfig
{
    /**
     * 一次合并的超时时间，单位为小时，默认为72小时，即表示一次合并的最大超时时间
     */
    private int timeout;
    
    /**
     * 分片任务的最大合并次数，最大的尝试次数，默认为3次
     */
    private int maxMergeTimes;
    
    /**
     * 分片任务的最大清理次数，最大的尝试次数，默认为3次
     */
    private int maxClearTimes;
    
    /**
     * 保留时长，单位为分钟：表示一个文件合并完后，多长时间后再删除老文件，默认1440分钟
     */
    private int reserveTime;
    
    /**
     * 最大耗时，每次执行清理操作，最大耗时时间，如果时间太长，则退出本来清理，等待下一次定时任务执行，单位为：毫秒
     */
    private long maxTimeConsuming;
    
    /**
     * 每次获取清理任务的最大数量
     */
    private int limit;
    
    /**
     * 每次清理一轮数据后的等待时间，单位毫秒，默认为1000毫秒，为了规避存储删除压力过大的问题
     */
    private long clearWait;
    
    public int getTimeout()
    {
        return timeout;
    }

    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }

    public int getMaxMergeTimes()
    {
        return maxMergeTimes;
    }

    public void setMaxMergeTimes(int maxMergeTimes)
    {
        this.maxMergeTimes = maxMergeTimes;
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

    public long getClearWait()
    {
        return clearWait;
    }

    public void setClearWait(long clearWait)
    {
        this.clearWait = clearWait;
    }

    public int getMaxClearTimes()
    {
        return maxClearTimes;
    }

    public void setMaxClearTimes(int maxClearTimes)
    {
        this.maxClearTimes = maxClearTimes;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }
}
