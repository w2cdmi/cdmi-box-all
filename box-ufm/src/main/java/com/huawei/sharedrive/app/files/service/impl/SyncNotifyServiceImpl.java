package com.huawei.sharedrive.app.files.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.event.domain.Event;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.SyncNotifyService;
import com.huawei.sharedrive.app.user.service.UserSyncVersionService;

@Component
public class SyncNotifyServiceImpl implements SyncNotifyService
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncNotifyServiceImpl.class);
    
    @Autowired
    private UserSyncVersionService userSyncVersionService;
    
    public void notifyUserCurrentSyncVersionChanged(INode iNode)
    {
        try
        {
            userSyncVersionService.notifyUserCurrentSyncVersionChanged(iNode.getOwnedBy(),
                iNode.getSyncVersion());
        }
        catch (Exception e)
        {
            LOGGER.warn(e.getMessage(), e);
        }
    }
    
    @Override
    public void consumeEvent(Event event)
    {
        if (event == null)
        {
            return;
        }
        if (event.getType() == EventType.OTHERS)
        {
            return;
        }
        INode node = null;
        if (event.getSource() != null)
        {
            node = event.getSource();
        }
        // 修改Coverity,全盘同步的节点不用通知
        if (null == node || node.getSyncStatus() == INode.SYNC_STATUS_BACKUP
            || node.getSyncStatus() == INode.SYNC_STATUS_EMAIL)
        {
            return;
        }
        switch (event.getType())
        {
            case INODE_CREATE:
            case INODE_CONTENT_CHANGE:
            case INODE_COPY:
            case INODE_PRELOAD_END:
            case TRASH_INODE_RECOVERY:
            case TRASH_RECOVERY:
            case INODE_RENAME:
            case INODE_MOVE:
            case INODE_DELETE:
            case INODE_UPDATE_SYNC:
            case INODE_UPDATE_NAME_SYNC:
                notifyUserCurrentSyncVersionChanged(node);
                break;
            case TRASH_CLEAR:
                // 清空回收站时传-1给PC客户端，通知其获取全量元数据
                node.setSyncVersion(INode.SYNC_VERSION_DELETE);
                notifyUserCurrentSyncVersionChanged(node);
                break;
            default:
                break;
        }
        
    }
    
    @Override
    public EventType[] getInterestedEvent()
    {
        return new EventType[]{EventType.INODE_CONTENT_CHANGE, EventType.INODE_COPY, EventType.INODE_CREATE,
            EventType.INODE_PRELOAD_END, EventType.TRASH_INODE_RECOVERY, EventType.INODE_RENAME,
            EventType.INODE_DELETE, EventType.TRASH_RECOVERY, EventType.INODE_MOVE,
            EventType.INODE_UPDATE_SYNC, EventType.INODE_UPDATE_NAME_SYNC, EventType.TRASH_CLEAR};
    }
    
}
