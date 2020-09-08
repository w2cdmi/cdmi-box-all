package com.huawei.sharedrive.app.mirror.manager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.event.domain.Event;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.event.service.EventConsumer;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

import pw.cdmi.core.utils.MethodLogAble;

/**
 * 消息处理
 * 
 * @author c00287749
 * 
 */
@Service("mirrorHandleForEvent")
public class CopyHandleForEvent implements EventConsumer
{
    @Autowired
    private CopyPolicyHandle copyPolicyHandle;
    
    @Autowired
    private CopyConfigLocalCache copyConfigLocalCache;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CopyHandleForEvent.class);
    
    private static final int MAX_QUEUE_SIZE = 10000;
    
    private ExecutorService executorService;
    
    private static LinkedBlockingQueue<Runnable> eventQueue = new LinkedBlockingQueue<Runnable>(
        MAX_QUEUE_SIZE);
    
    @PostConstruct
    public void init()
    {
        executorService = new ThreadPoolExecutor(5, 10, 0L, TimeUnit.MILLISECONDS, eventQueue);
    }
    
    @MethodLogAble
    @Override
    public void consumeEvent(Event event)
    {
        if (eventQueue.size() >= MAX_QUEUE_SIZE)
        {
            LOGGER.info("task eventQueue is up limit,size ," + eventQueue.size());
            return;
        }
        
        if (EventType.INODE_CREATE == event.getType() || EventType.INODE_PRELOAD_END == event.getType()
            || EventType.INODE_CONTENT_CHANGE == event.getType())
        {
            INode iNode = event.getSource();
            if (null == iNode)
            {
                return;
            }
            
            if (INode.STATUS_DELETE == iNode.getStatus() || INode.STATUS_CREATING == iNode.getStatus())
            {
                LOGGER.info("iNode state error ,state:" + iNode.getStatus());
                return;
            }
            executorService.execute(new HandleWorker(event));
        }
        else if (EventType.INODE_COPY == event.getType() || EventType.INNER_DEDUP == event.getType())
        {
            executorService.execute(new HandleWorker(event));
        }
        else
        {
            return;
        }
        
    }
    
    public void handleEvent(Event event)
    {
        if (EventType.INODE_CREATE == event.getType() || EventType.INODE_PRELOAD_END == event.getType()
            || EventType.INODE_CONTENT_CHANGE == event.getType())
        {
            handleCreateMsg(event);
        }
        else if (EventType.INODE_COPY == event.getType())
        {
            handleCopyMsg(event);
        }
        else if (EventType.INNER_DEDUP == event.getType())
        {
            handleDedupMsg(event);
        }
        else
        {
            return;
        }
    }
    
    private final class HandleWorker implements Runnable
    {
        private Event event;
        
        HandleWorker(Event event)
        {
            this.event = event;
        }
        
        @Override
        public void run()
        {
            try
            {
                handleEvent(event);
            }
            catch (Exception e)
            {
                LOGGER.warn(e.getMessage(), e);
            }
            
        }
    }
    
    /**
     * 处理创建消息
     * 
     * @param event
     */
    private void handleCreateMsg(Event event)
    {
        LOGGER.debug("handleCreateMsg begion");
        
        if (!copyConfigLocalCache.isSystemMirrorEnable())
        {
            LOGGER.info("mirror_global_enable is false");
            return;
        }
        
        if (!copyConfigLocalCache.isSystemMirrorTimerEnable())
        {
            LOGGER.info("this System mirror time enable is false");
            return;
        }
        
        if (!copyConfigLocalCache.isAllowCreateTaskByDB())
        {
            LOGGER.info("copy_task total>1000000 ,not reveice msg");
            return;
        }
        
        // 检测节点
        if (event == null || null == event.getSource())
        {
            LOGGER.info("event is null or inode is null");
            return;
        }
        
        INode iNode = event.getSource();
        LOGGER.info("handleCreateMsg file objectid is :" + iNode.getObjectId() + " and own is :"
            + iNode.getOwnedBy());
        if (FilesCommonUtils.isFolderType(iNode.getType()))
        {
            // 创建文件夹消息不处理
            LOGGER.info("event is create folder ");
            return;
        }
        
        // 创建任务
        copyPolicyHandle.buildCopyTaskForCreateFileNode(iNode);
        LOGGER.debug("handleCreateMsg end");
    }
    
    /**
     * 处理复制消息
     * 
     * @param event
     */
    private void handleCopyMsg(Event event)
    {
        LOGGER.debug("handleCopyMsg begion");
        
        if (!copyConfigLocalCache.isSystemMirrorEnable())
        {
            LOGGER.info("mirror_global_enable is false");
            return;
        }
        
        if (!copyConfigLocalCache.isSystemMirrorTimerEnable())
        {
            LOGGER.info("this System mirror time enable is false");
            return;
        }
        
        if (!copyConfigLocalCache.isAllowCreateTaskByDB())
        {
            LOGGER.info("copy_task total>1000000 ,not reveice msg");
            return;
        }
        
        // 检测节点
        if (event == null || null == event.getSource() || null == event.getDest())
        {
            return;
        }
        
        INode srcINode = event.getSource();
        
        INode destINode = event.getDest();
        
        if (FilesCommonUtils.isFolderType(srcINode.getType()))
        {
            // 需要批量处理创建文件夹;
            copyPolicyHandle.buildCopyTaskForCopyFolderNode(destINode);
            
        }
        else if (INode.TYPE_FILE == srcINode.getType() || INode.TYPE_VERSION == srcINode.getType())
        {
            // 创建任务
            copyPolicyHandle.buildCopyTaskForCreateFileNode(destINode);
        }
        
        LOGGER.debug("handleCopyMsg end");
        
    }
    
    private void handleDedupMsg(Event event)
    {
        // 检测节点
        if (event == null || null == event.getSource() || null == event.getDest())
        {
            return;
        }
        
        INode srcINode = event.getSource();
        
        INode destINode = event.getDest();
        LOGGER.info("mirror handle Dedup Msg, srcObjectId:" + srcINode.getObjectId() + " destObjectId:"
            + destINode.getObjectId());
        copyPolicyHandle.handleDedupMsg(srcINode, destINode);
    }
    
    @Override
    public EventType[] getInterestedEvent()
    {
        return new EventType[]{EventType.INODE_COPY, EventType.INODE_CREATE, EventType.INODE_PRELOAD_END,
            EventType.INODE_CONTENT_CHANGE, EventType.INNER_DEDUP};
    }
    
}
