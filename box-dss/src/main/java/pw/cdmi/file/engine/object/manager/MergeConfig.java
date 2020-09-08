/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.file.engine.filesystem.model.FSDefinition;

/**
 * 
 * @author s90006125
 *
 */
public class MergeConfig
{
    /**
     * 一次合并的超时时间，单位为小时，默认为72小时，即表示一次合并的最大超时时间
     */
    private int timeout;
    
    /**
     * 合并失败的尝试次数，默认为3次
     */
    private int maxMergeTimes;
    
    private boolean needDeleteParts;
    // 标识最小的需要合并的分片数，如果一个文件的分片数大于该值，则需要合并
    private int minPartSizeForMerge;
    private Map<String, String> needMergeMap = new ConcurrentHashMap<String, String>(1);
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
    public boolean isNeedDeleteParts()
    {
        return needDeleteParts;
    }
    public void setNeedDeleteParts(boolean needDeleteParts)
    {
        this.needDeleteParts = needDeleteParts;
    }
    public int getMinPartSizeForMerge()
    {
        return minPartSizeForMerge;
    }
    public void setMinPartSizeForMerge(int minPartSizeForMerge)
    {
        this.minPartSizeForMerge = minPartSizeForMerge;
    }
    public Map<String, String> getNeedMerge()
    {
        return needMergeMap;
    }
    public void setNeedMerge(Map<String, String> needMerge)
    {
        this.needMergeMap = needMerge;
    }
    
    public boolean needMerge(FSDefinition fileSystem)
    {
        String temp = needMergeMap.get(fileSystem.getType());
        if(StringUtils.isBlank(temp))
        {
            return false;
        }
        return Boolean.parseBoolean(temp);
    }
}
