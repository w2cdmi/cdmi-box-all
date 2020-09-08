package pw.cdmi.box.disk.client.domain.node;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.box.disk.utils.FilesCommonUtils;
import pw.cdmi.core.exception.BadRquestException;
import pw.cdmi.core.exception.BaseRunException;

public class FilePreUploadRequest
{
    private Long contentCreatedAt;
    
    private Long contentModifiedAt;
    
    private String encryptKey;
    
    private String name;
    
    private String path;
    
    private Long parent;
    
    private String sha1;
    
    private Long size;
    
    public FilePreUploadRequest()
    {
    }
    
    public FilePreUploadRequest(String name, Long parent, Long size)
    {
        this.name = name;
        this.parent = parent;
        this.size = size;
    }
    
    public void checkParameter() throws BadRquestException
    {
        FilesCommonUtils.checkNonNegativeIntegers(parent, size);
        
        FilesCommonUtils.checkNodeNameVaild(name);
        
        FilesCommonUtils.checkVaildSha1(sha1);
        
        if (contentCreatedAt != null)
        {
            FilesCommonUtils.checkNonNegativeIntegers(contentCreatedAt);
        }
        
        if (contentModifiedAt != null)
        {
            FilesCommonUtils.checkNonNegativeIntegers(contentModifiedAt);
        }
        
        if (StringUtils.isNotBlank(encryptKey))
        {
            FilesCommonUtils.checkEncryptKey(encryptKey);
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
    
    public String getEncryptKey()
    {
        return encryptKey;
    }
    
    public String getName()
    {
        return name;
    }
    
    public Long getParent()
    {
        return parent;
    }
    
    public String getSha1()
    {
        return sha1;
    }
    
    public Long getSize()
    {
        return size;
    }
    
    public void setContentCreatedAt(Long contentCreatedAt)
    {
        this.contentCreatedAt = contentCreatedAt;
    }
    
    public void setContentModifiedAt(Long contentModifiedAt)
    {
        this.contentModifiedAt = contentModifiedAt;
    }
    
    public void setEncryptKey(String encryptKey)
    {
        this.encryptKey = encryptKey;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setParent(Long parent)
    {
        this.parent = parent;
    }
    
    public void setSha1(String sha1)
    {
        this.sha1 = sha1;
    }
    
    public void setSize(Long size)
    {
        this.size = size;
    }
    
    public INode transToINode() throws BaseRunException
    {
        INode fileNode = new INode();
        fileNode.setParentId(parent);
        fileNode.setSha1(sha1);
        fileNode.setSize(size);
        fileNode.setName(name);
        fileNode.setType(INode.TYPE_FILE);
        
        if (null != contentCreatedAt)
        {
            fileNode.setContentCreatedAt(new Date(contentCreatedAt));
        }
        if (null != contentModifiedAt)
        {
            fileNode.setContentModifiedAt(new Date(contentModifiedAt));
        }
        
        if (StringUtils.isNotBlank(encryptKey))
        {
            fileNode.setEncryptKey(encryptKey);
        }
        
        return fileNode;
    }

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
    
    
}
