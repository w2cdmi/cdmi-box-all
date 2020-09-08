package com.huawei.sharedrive.app.openapi.domain.node;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

public class RestFilesCopyMoveRequest
{
    
    private Long destParent;
    
    private String name;
    
    public void checkParameter() throws BaseRunException
    {
        FilesCommonUtils.checkNonNegativeIntegers(destParent);
        
        if (null != name)
        {
            FilesCommonUtils.checkNodeNameVaild(name);
        }
    }
    
    public INode getDestINode()
    {
        INode destNode = new INode();
        if (destParent == null)
        {
            throw new InvalidParamException("destParent can not be null");
        }
        destNode.setId(destParent);
        return destNode;
    }
    
    public Long getDestParent()
    {
        return destParent;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setDestParent(Long destParent)
    {
        this.destParent = destParent;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
}
