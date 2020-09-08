package com.huawei.sharedrive.app.openapi.domain.node;

import java.util.ArrayList;
import java.util.List;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

public class FileMultiPreUploadRequest
{
    private Long tokenTimeout;
    
    private Long parent;
    
    private List<FilePreUploadBaseRequest> files;
    
    public void checkParamter() throws BaseRunException
    {
        if (tokenTimeout != null && tokenTimeout <= 0)
        {
            throw new InvalidParamException("tokenTimeout:" + tokenTimeout);
        }
        
        FilesCommonUtils.checkNonNegativeIntegers(parent);
        if (files == null)
        {
            throw new InvalidParamException("files can not be null");
        }
        
        if (files.isEmpty())
        {
            throw new InvalidParamException("files.size() == 0");
        }
        
        if (files.size() > 1000)
        {
            throw new InvalidParamException("size exceed 1000:" + 1000);
        }
        
        for (FilePreUploadBaseRequest item : files)
        {
            item.checkParamter();
        }
    }

    public Long getTokenTimeout()
    {
        return tokenTimeout;
    }


    public void setTokenTimeout(Long tokenTimeout)
    {
        this.tokenTimeout = tokenTimeout;
    }

    public Long getParent()
    {
        return parent;
    }

    public void setParent(Long parent)
    {
        this.parent = parent;
    }

    public List<FilePreUploadBaseRequest> getFiles()
    {
        return files;
    }

    public void setFiles(List<FilePreUploadBaseRequest> files)
    {
        this.files = files;
    }
    
    public List<INode> transToINode(long ownedBy) throws BaseRunException
    {
        if (files == null)
        {
            throw new InvalidParamException("files can not be null");
        }
        if (files.isEmpty())
        {
            throw new InvalidParamException("files.size() == 0");
        }
        if (parent == null)
        {
            throw new InvalidParamException("parent can not be null");
        }
        List<INode> result = new ArrayList<INode>(files.size());
        INode tempNode;
        for (FilePreUploadBaseRequest item : files)
        {
            tempNode = item.transToINode();
            tempNode.setOwnedBy(ownedBy);
            tempNode.setParentId(parent);
            result.add(tempNode);
        }
        return result;
    }
    
}
