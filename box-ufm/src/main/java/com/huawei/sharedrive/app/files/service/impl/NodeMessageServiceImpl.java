package com.huawei.sharedrive.app.files.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.acl.service.INodeACLService;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.NodeMessageService;
import com.huawei.sharedrive.app.share.domain.INodeShare;
import com.huawei.sharedrive.app.share.service.LinkServiceV2;
import com.huawei.sharedrive.app.share.service.ShareService;
import com.huawei.sharedrive.app.user.service.UserSyncVersionService;

@Component
@Service("nodeMessageService")
public class NodeMessageServiceImpl implements NodeMessageService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeMessageServiceImpl.class);
    
    @Autowired
    private LinkServiceV2 linkServiceV2;
    
    @Autowired
    private INodeACLService nodeACLService;
    
    @Autowired
    private ShareService shareService;
    
    
    @Autowired
    private UserSyncVersionService userSyncVersionService;
    
    
    /**
     * 通知删除消息到Link模块
     * 
     * @param iNode
     */
    @Override
    public void notifyACLToDeleteMsg(INode iNode)
    {
        try
        {
            if (iNode != null)
            {
                nodeACLService.deleteINodeAllACLs(iNode.getOwnedBy(), iNode.getId());
            }
        }
        catch (Exception e)
        {
            LOGGER.warn(e.getMessage(), e);
        }
        
    }
    
    /**
     * 通知删除消息到共享模块
     * 
     * @param iNode
     */
    @Override
    public void notifyLinkToDeleteMsg(INode iNode)
    {
        // 如果设置外链，则通知
        try
        {
            if (iNode != null)
            {
                linkServiceV2.deleteAllLinkByNode(iNode);
            }
        }
        catch (Exception e)
        {
            LOGGER.warn(e.getMessage(), e);
        }
        
    }
    
    /**
     * 通知恢复消息到共享模块
     * 
     * @param iNode
     */
    @Override
    public void notifyRestoreShareMsg(INode iNode,long creatdBy)
    {
        // 如果设置共享，则通知共享消息
        try
        {
            if (iNode != null && INode.SHARE_STATUS_SHARED == iNode.getShareStatus())
            {
                shareService.updateStatus(iNode,creatdBy, INodeShare.STATUS_NORMAL);
            }
        }
        catch (Exception e)
        {
            LOGGER.warn(e.getMessage(), e);
        }
        
    }
    
    /**
     * 通知删除消息到共享模块
     * 
     * @param iNode
     */
    @Override
    public void notifyShareToDeleteMsg(INode iNode,long creatdBy)
    {
        // 如果设置共享，则通知共享消息
        try
        {
            if (iNode != null && INode.SHARE_STATUS_SHARED == iNode.getShareStatus())
            {
                shareService.deleteAllShare(iNode,creatdBy);
            }
        }
        catch (Exception e)
        {
            LOGGER.warn(e.getMessage(), e);
        }
        
    }
    
    /**
     * 通知rename消息到共享模块
     * 
     * @param iNode
     */
    @Override
    public void notifyShareToUpdateMsg(INode iNode,long createdBy)
    {
        // 如果设置共享，则通知共享消息
        try
        {
            if (iNode != null && INode.SHARE_STATUS_SHARED == iNode.getShareStatus())
            {
                shareService.updateNodeNameAndSize(iNode,createdBy);
            }
        }
        catch (Exception e)
        {
            LOGGER.warn(e.getMessage(), e);
        }
    }
    
    /**
     * 通知删除消息到共享模块
     * 
     * @param iNode
     */
    @Override
    public void notifyShareToTrashMsg(INode iNode,long creatdBy)
    {
        // 如果设置共享，则通知共享消息
        try
        {
            if (iNode != null && INode.SHARE_STATUS_SHARED == iNode.getShareStatus())
            {
                shareService.updateStatus(iNode,creatdBy, INodeShare.STATUS_IN_RECYCLE);
            }
        }
        catch (Exception e)
        {
            LOGGER.warn(e.getMessage(), e);
        }
    }
    
    /**
     * 通知客户端端同步版本发生改变
     * 
     * @param iNode
     */
    @Override
    public void notifyUserCurrentSyncVersionChanged(INode iNode)
    {
        try
        {
            userSyncVersionService.notifyUserCurrentSyncVersionChanged(iNode.getOwnedBy(),
                iNode.getSyncVersion());
            LOGGER.info("notifyUserCurrentSyncVersionChanged end,userid: " + iNode.getOwnedBy() + ",id:"
                + iNode.getId() + ",ver:" + iNode.getSyncVersion());
        }
        catch (Exception e)
        {
            LOGGER.warn(e.getMessage(), e);
        }
    }
    
}
