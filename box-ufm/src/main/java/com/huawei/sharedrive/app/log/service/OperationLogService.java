package com.huawei.sharedrive.app.log.service;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.event.domain.Event;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.event.service.EventConsumer;
import com.huawei.sharedrive.app.log.domain.FullViewLogID;

import pw.cdmi.core.utils.RandomGUID;

@Service
public class OperationLogService implements EventConsumer
{
    
    /**
     * 日志记录量
     */
    private static ThreadLocal<Map<String, String>> logThreadLocal = new ThreadLocal<Map<String, String>>();
    
    @Value("${log.fullview.innerid}")
    private String innerID;
    
    
    /**
     * 追加当前节点日志
     * 
     * @param fullViewLogID
     * @return
     */
    public String appendID(String sourceLogID)
    {
        FullViewLogID logID = new FullViewLogID(sourceLogID);
        logID.appendID(getGourpID(), innerID, new Date());
        String fullViewLogID = logID.toString();
        logThreadLocal.get().put("FullViewLogID", fullViewLogID);
        return fullViewLogID;
    }
    
    @Override
    public void consumeEvent(Event event)
    {
        // TODO Auto-generated method stub
    }
    
    /**
     * 生成全局日志ID
     * 
     * @return
     */
    public String generateFullViewLogID()
    {
        String randomId = new RandomGUID().getValueAfterMD5().substring(0, 16);
        
        FullViewLogID logID = new FullViewLogID(randomId, getGourpID(), innerID, new Date());
        String fullViewLogID = logID.toString();
        logThreadLocal.get().put("FullViewLogID", fullViewLogID);
        return fullViewLogID;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.huawei.sharedrive.app.event.service.EventConsumer#getInterestedEvent()
     */
    @Override
    public EventType[] getInterestedEvent()
    {
        // TODO Auto-generated method stub
        return new EventType[]{};
    }
    
    public String mergeID(String dest, String src)
    {
        // 合并日志ID
        return null;
    }
    
    /**
     * 
     */
    public void recordLog()
    {
        // TODO 记录日志
    }
    
    public void recordLog(String message)
    {
        // TODO 记录日志
    }
    
    /**
     * 获取GroupID, 也就是DCID,由申卿统一提供接口获取(AppServer 默认成00)
     * 
     * @return
     */
    private String getGourpID()
    {
        // TODO GroupID 也就是DCID,由申卿统一提供接口获取(AppServer 默认成00)
        String groupID = "00";
        return groupID;
    }
    
}
