package com.huawei.sharedrive.stub.nodecreate;

import java.sql.Date;
import java.util.UUID;

import com.huawei.sharedrive.app.files.domain.INode;

public class INodeFileBuilder
{
    
    public static INode getFile(INode parentNode, byte syncStatus, boolean isBack)
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
        inode.setType(INode.TYPE_FILE);
        inode.setSize(102540L);
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
        inode.setMd5(UUID.randomUUID().toString());
        inode.setSha1(inode.getMd5());
        return inode;
    }
    
}
