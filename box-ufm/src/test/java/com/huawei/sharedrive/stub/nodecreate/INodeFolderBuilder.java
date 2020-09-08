package com.huawei.sharedrive.stub.nodecreate;

import java.sql.Date;

import com.huawei.sharedrive.app.files.domain.INode;

public class INodeFolderBuilder
{
    public static INode getBackComputerFolder(long ownerId)
    {
        Date now = new Date(System.currentTimeMillis());
        INode inode = new INode();
        inode.setId(NodeIdGenerator.getNextBackNodeId());
        inode.setParentId(0L);
        inode.setName(FieldHelper.getRandomFolderName());
        inode.setType((byte)-2);
        inode.setSize(0L);
        inode.setStatus(INode.STATUS_NORMAL);
        inode.setVersion("3");
        inode.setOwnedBy(ownerId);
        inode.setCreatedBy(ownerId);
        inode.setModifiedBy(ownerId);
        inode.setContentCreatedAt(now);
        inode.setContentModifiedAt(now);
        inode.setCreatedAt(now);
        inode.setModifiedAt(now);
        inode.setShareStatus((byte)0);
        inode.setSyncStatus((byte)3);
        inode.setSyncVersion(332);
        inode.setSecurityId((byte)0);
        inode.setResourceGroupId(2);
        return inode;
    }
    
    public static INode getBackFolder(INode parentNode, byte syncStatus, boolean isBack)
    {
        Date now = new Date(System.currentTimeMillis());
        INode inode = new INode();
        if(isBack)
        {
            inode.setId(NodeIdGenerator.getNextBackNodeId());
        }
        else
        {
            inode.setId(NodeIdGenerator.getNextNormailNodeId());
        }
        inode.setParentId(parentNode.getId());
        inode.setName(FieldHelper.getRandomFolderName());
        inode.setType(INode.TYPE_FOLDER);
        inode.setSize(0L);
        inode.setStatus(INode.STATUS_NORMAL);
        inode.setVersion("3");
        inode.setOwnedBy(parentNode.getOwnedBy());
        inode.setCreatedBy(parentNode.getOwnedBy());
        inode.setModifiedBy(parentNode.getOwnedBy());
        inode.setContentCreatedAt(now);
        inode.setContentModifiedAt(now);
        inode.setCreatedAt(now);
        inode.setModifiedAt(now);
        inode.setShareStatus((byte)0);
        inode.setSyncStatus(syncStatus);
        inode.setSyncVersion(332);
        inode.setSecurityId((byte)0);
        inode.setResourceGroupId(2);
        return inode;
    }
    
    public static INode getBackDiskFolder(INode parentNode)
    {
        Date now = new Date(System.currentTimeMillis());
        INode inode = new INode();
        inode.setId(NodeIdGenerator.getNextBackNodeId());
        inode.setParentId(parentNode.getId());
        inode.setName(FieldHelper.getRandomFolderName());
        inode.setType((byte)-3);
        inode.setSize(0L);
        inode.setStatus(INode.STATUS_NORMAL);
        inode.setVersion("3");
        inode.setOwnedBy(parentNode.getOwnedBy());
        inode.setCreatedBy(parentNode.getOwnedBy());
        inode.setModifiedBy(parentNode.getOwnedBy());
        inode.setContentCreatedAt(now);
        inode.setContentModifiedAt(now);
        inode.setCreatedAt(now);
        inode.setModifiedAt(now);
        inode.setShareStatus((byte)0);
        inode.setSyncStatus((byte)1);
        inode.setSyncVersion(332);
        inode.setSecurityId((byte)0);
        inode.setResourceGroupId(2);
        return inode;
    }
    
}
