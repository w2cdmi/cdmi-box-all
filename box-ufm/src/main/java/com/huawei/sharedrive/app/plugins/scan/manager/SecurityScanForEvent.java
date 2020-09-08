package com.huawei.sharedrive.app.plugins.scan.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.event.domain.Event;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.event.service.EventConsumer;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.plugins.scan.domain.SecurityScanTask;

import pw.cdmi.core.utils.MethodLogAble;

@Service("securityScanForEvent")
public class SecurityScanForEvent implements EventConsumer
{
    @Autowired
    private SecurityScanManager securityScanManager;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityScanForEvent.class);
    
    @MethodLogAble
    @Override
    public void consumeEvent(Event event)
    {
        if (event == null)
        {
            LOGGER.warn("event is  null");
            return;
        }
        if (EventType.INNER_DEDUP == event.getType())
        {
            
            // 检测节点
            if (null == event.getSource() || null == event.getDest())
            {
                LOGGER.warn("event is  null");
                return;
            }
            
            INode srcINode = event.getSource();
            
            // 产生新任务
            securityScanManager.sendScanTask(srcINode, SecurityScanTask.PRIORITY_HIGH);
            
        }
        
    }
    
    @Override
    public EventType[] getInterestedEvent()
    {
        return new EventType[]{EventType.INNER_DEDUP};
    }
    
}
