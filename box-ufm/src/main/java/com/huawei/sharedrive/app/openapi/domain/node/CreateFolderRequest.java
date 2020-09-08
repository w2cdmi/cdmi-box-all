package com.huawei.sharedrive.app.openapi.domain.node;

import java.util.Date;

import com.huawei.sharedrive.app.exception.BadRequestException;
import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

import pw.cdmi.core.utils.TrimUtils;

public class CreateFolderRequest
{
    private Long contentCreatedAt;
    
    private Long contentModifiedAt;
    
    private String name;
    
    private Long parent;
    
    private String extraType;
    
    private Boolean autoMerge;
    
    public void checkParameter() throws BaseRunException
    {
        FilesCommonUtils.checkNonNegativeIntegers(parent);
        
        FilesCommonUtils.checkNodeNameVaild(name);
        
        if (contentCreatedAt != null)
        {
            FilesCommonUtils.checkNonNegativeIntegers(contentCreatedAt);
        }
        
        if (contentModifiedAt != null)
        {
            FilesCommonUtils.checkNonNegativeIntegers(contentModifiedAt);
        }
        if (extraType != null)
        {
            if (!FilesCommonUtils.isValidFolderExtArr(extraType))
            {
                throw new InvalidParamException("extraType is invalid:" + extraType);
            }
        }
        
    }
    
    public INode transToINode() throws BaseRunException
    {
        try
        {
            INode fileNode = new INode();
            fileNode.setName(getName());
            fileNode.setParentId(getParent());
            if (getContentCreatedAt() != null)
            {
                fileNode.setContentCreatedAt(new Date(getContentCreatedAt()));
            }
            
            if (getContentModifiedAt() != null)
            {
                fileNode.setContentModifiedAt(new Date(getContentModifiedAt()));
            }
            if (extraType != null)
            {
                if (INode.TYPE_BACKUP_COMPUTER_STR.equals(extraType))
                {
                    fileNode.setType(INode.TYPE_BACKUP_COMPUTER);
                }
                else if (INode.TYPE_BACKUP_DISK_STR.equals(extraType))
                {
                    fileNode.setType(INode.TYPE_BACKUP_DISK);
                }
                else if (INode.TYPE_BACKUP_EMAIL_STR.equals(extraType))
                {
                    fileNode.setType(INode.TYPE_BACKUP_EMAIL);
                }
            }
            else
            {
                fileNode.setType(INode.TYPE_FOLDER);
            }
            return fileNode;
        }
        catch (RuntimeException e)
        {
            throw new BadRequestException(e);
        }
        catch (Exception e)
        {
            throw new BadRequestException(e);
        }
        
    }
    
    public String getExtraType()
    {
        return extraType;
    }
    
    public void setExtraType(String extraType)
    {
        this.extraType = extraType;
    }
    
    public Long getContentCreatedAt()
    {
        return contentCreatedAt;
    }
    
    public Long getContentModifiedAt()
    {
        return contentModifiedAt;
    }
    
    public String getName()
    {
        return name;
    }
    
    public Long getParent()
    {
        return parent;
    }
    
    public void setContentCreatedAt(Long contentCreatedAt)
    {
        this.contentCreatedAt = contentCreatedAt;
    }
    
    public void setContentModifiedAt(Long contentModifiedAt)
    {
        this.contentModifiedAt = contentModifiedAt;
    }
    
    public void setName(String name)
    {
        this.name = StringUtils.isNotBlank(name) ? TrimUtils.trimToEmpty(name) : name;
    }
    
    public void setParent(Long parent)
    {
        this.parent = parent;
    }
    
    public Boolean getAutoMerge()
    {
        return autoMerge;
    }
    
    public void setAutoMerge(Boolean autoMerge)
    {
        this.autoMerge = autoMerge;
    }
    
    public Boolean getMergeValue()
    {
        if (autoMerge != null)
        {
            return autoMerge;
        }
        return Boolean.FALSE;
    }
    
}
