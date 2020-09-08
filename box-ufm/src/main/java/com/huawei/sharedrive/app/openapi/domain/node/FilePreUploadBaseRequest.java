package com.huawei.sharedrive.app.openapi.domain.node;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

public class FilePreUploadBaseRequest
{
    private String blockMD5;
    
    private Long contentCreatedAt;
    
    private Long contentModifiedAt;
    
    private String encryptKey;
    
    private String md5;
    
    private String name;
    
    private String sha1;
    
    private Long size;
    
    private Long createdBy;
    
    public void checkParamter() throws BaseRunException
    {
        FilesCommonUtils.checkNonNegativeIntegers(size);
        
        FilesCommonUtils.checkNodeNameVaild(name);
        
        if (StringUtils.isNotBlank(sha1))
        {
            FilesCommonUtils.checkVaildSha1(sha1);
        }
        if (StringUtils.isNotBlank(md5))
        {
            FilesCommonUtils.checkVaildMD5(md5);
        }
        if (StringUtils.isNotBlank(blockMD5))
        {
            FilesCommonUtils.checkVaildMD5(blockMD5);
        }
        if (StringUtils.isNotBlank(encryptKey))
        {
            FilesCommonUtils.checkEncryptKey(encryptKey);
        }
        
    }
    
    public String getBlockMD5()
    {
        return blockMD5;
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
    
    public String getMd5()
    {
        return md5;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getSha1()
    {
        return sha1;
    }
    
    public Long getSize()
    {
        return size;
    }
    
    public void setBlockMD5(String blockMD5)
    {
        this.blockMD5 = blockMD5;
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
    
    public void setMd5(String md5)
    {
        this.md5 = md5;
    }
    
    public void setName(String name)
    {
        this.name = name;
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
		fileNode.setSha1(StringUtils.isBlank(sha1) ? "" : sha1);
		if (size == null) {
			throw new InvalidParamException("size can not be null");
		}
		fileNode.setSize(size);
		fileNode.setName(name);
		fileNode.setType(INode.TYPE_FILE);
		fileNode.setMd5(md5);
		fileNode.setBlockMD5(blockMD5);


		if (null != createdBy) {
			fileNode.setCreatedBy(createdBy);
			fileNode.setModifiedBy(createdBy);
		}
		
		if (null != contentCreatedAt) {
			fileNode.setContentCreatedAt(new Date(contentCreatedAt));
		}
		if (null != contentModifiedAt) {
			fileNode.setContentModifiedAt(new Date(contentModifiedAt));
		}

		if (StringUtils.isNotBlank(encryptKey)) {
			fileNode.setEncryptKey(encryptKey);
		}

		return fileNode;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}
    
    
}
