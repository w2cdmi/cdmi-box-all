package com.huawei.sharedrive.app.files.service;

import com.huawei.sharedrive.app.files.domain.INode;

public interface NodeMessageService
{
    
    void notifyACLToDeleteMsg(INode iNode);
    
    void notifyLinkToDeleteMsg(INode iNode);
    
    void notifyRestoreShareMsg(INode iNode,long createBy);
    
    void notifyUserCurrentSyncVersionChanged(INode iNode);

	void notifyShareToTrashMsg(INode iNode, long creatdBy);

	void notifyShareToDeleteMsg(INode iNode, long creatdBy);

	void notifyShareToUpdateMsg(INode iNode, long createdBy);
}
