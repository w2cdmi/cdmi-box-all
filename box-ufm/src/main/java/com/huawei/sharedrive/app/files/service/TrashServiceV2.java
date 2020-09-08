package com.huawei.sharedrive.app.files.service;

import java.util.List;

import com.huawei.sharedrive.app.core.domain.Thumbnail;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.domain.FileINodesList;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;

import pw.cdmi.box.domain.Order;

public interface TrashServiceV2
{
    /**
     * 清空回收站，将回收站下的节点状态为彻底删除状态
     * 
     * @param user
     * @param ownerId
     * @throws BaseRunException
     */
    INode cleanTrash(UserToken user, Long ownerId) throws BaseRunException;
    
    /**
     * 彻底删除回收站的状态节点
     * 
     * @param user
     * @param iNode
     * @throws BaseRunException
     */
    void deleteTrashItem(UserToken user, INode iNode) throws BaseRunException;
    
    /**
     * 列举回收站
     * 
     * @param user
     * @param ownerId
     * @param limit
     * @param offset
     * @param orderList
     * @param thumbnailList
     * @return
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    FileINodesList listTrashItems(UserToken user, long ownerId, int limit, long offset,
        List<Order> orderList, List<Thumbnail> thumbnailList, boolean withExtraType) throws BaseRunException;
    
    /**
     * 恢复回收站指定节点:该方法不能添加事务,否则会导致AsyncTrashRestoreJob异常
     * 
     * @param user
     * @param fileNode
     * @param newName
     * @param parentNode
     * @throws BaseRunException
     */
    void restoreTrashItem(UserToken user, INode fileNode, String newName, INode parentNode)
        throws BaseRunException;
}
