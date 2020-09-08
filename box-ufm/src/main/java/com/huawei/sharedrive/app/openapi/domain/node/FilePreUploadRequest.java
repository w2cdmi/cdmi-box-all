package com.huawei.sharedrive.app.openapi.domain.node;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

public class FilePreUploadRequest extends FilePreUploadBaseRequest
{
    private Long tokenTimeout;
    
    private Long parent;
    
    private String path;
    
    public void checkParamter() throws BaseRunException
    {
        super.checkParamter();
        if (tokenTimeout != null && tokenTimeout <= 0)
        {
            throw new InvalidParamException("tokenTimeout:" + tokenTimeout);
        }
        FilesCommonUtils.checkNonNegativeIntegers(parent);
    }
    
    public void setParent(Long parent)
    {
        this.parent = parent;
    }
    
    public Long getParent()
    {
        return parent;
    }

    public Long getTokenTimeout()
    {
        return tokenTimeout;
    }


    public void setTokenTimeout(Long tokenTimeout)
    {
        this.tokenTimeout = tokenTimeout;
    }
    
    public INode transToINode() throws BaseRunException
    {
        INode fileNode = super.transToINode();
        if (parent == null)
        {
            throw new InvalidParamException("parent can not be null");
        }
        fileNode.setParentId(parent);
        
        return fileNode;
    }

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
    
}
