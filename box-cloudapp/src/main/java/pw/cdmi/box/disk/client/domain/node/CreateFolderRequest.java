package pw.cdmi.box.disk.client.domain.node;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.box.disk.utils.FilesCommonUtils;
import pw.cdmi.core.exception.BadRquestException;
import pw.cdmi.core.exception.BaseRunException;

public class CreateFolderRequest
{
    private Long contentCreatedAt;
    
    private Long contentModifiedAt;
    
    private String name;
    
    private Long parent;
    
    private final static Logger LOGGER = LoggerFactory.getLogger(CreateFolderRequest.class);
    
    public CreateFolderRequest()
    {
        
    }
    
    public CreateFolderRequest(String name, Long parent)
    {
        this.name = name;
        this.parent = parent;
    }
    
    public void checkParameter() throws BadRquestException
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
        this.name = StringUtils.isNotBlank(name) ? name.trim() : name;
    }
    
    public void setParent(Long parent)
    {
        this.parent = parent;
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
            
            return fileNode;
        }
        catch (Exception e)
        {
            LOGGER.error("fail in transToINode", e);
            throw new BadRquestException(e);
        }
        
    }
    
}
