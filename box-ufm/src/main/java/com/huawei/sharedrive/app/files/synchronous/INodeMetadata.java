package com.huawei.sharedrive.app.files.synchronous;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.exception.InternalServerErrorException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.utils.PropertiesUtils;

public class INodeMetadata
{
    
    private Long contentCreatedAt;
    
    private Long contentModifiedAt;
    
    private Long createdAt;
    
    private long id;
    
    private Long modifiedAt;
    
    private String name;
    
    private String objectId;
    
    private long ownedBy;
    
    private long parentId;
    
    private String sha1 = "";
    
    private long size;
    
    /**
     * 节点状态：0-正常
     */
    private byte status;
    
    private int syncStatus;
    
    /**
     * 同步版本
     */
    private long syncVersion;
    
    /**
     * 节点类型，0-目录；1-文件；2-文件版本
     */
    private byte type;
    
    public INodeMetadata(INode node)
    {
        this.id = node.getId();
        this.type = node.getType();
        this.name = node.getName();
        this.parentId = node.getParentId();
        this.size = node.getSize();
        this.status = node.getStatus();
        this.ownedBy = node.getOwnedBy();
        this.objectId = node.getObjectId();
        this.sha1 = node.getSha1();
        this.syncVersion = node.getSyncVersion();
        this.syncStatus = node.getSyncStatus();
        
        if (null != node.getCreatedAt())
        {
            this.createdAt = node.getCreatedAt().getTime();
        }
        
        if (null != node.getModifiedAt())
        {
            this.modifiedAt = node.getModifiedAt().getTime();
        }
        
        if (null != node.getContentCreatedAt())
        {
            this.contentCreatedAt = node.getContentCreatedAt().getTime();
        }
        
        if (null != node.getContentModifiedAt())
        {
            this.contentModifiedAt = node.getContentModifiedAt().getTime();
        }
        
    }
    
    public INodeMetadata()
    {

    }
    
    public Long getContentCreatedAt()
    {
        return contentCreatedAt;
    }
    
    public Long getContentModifiedAt()
    {
        return contentModifiedAt;
    }
    
    public Long getCreatedAt()
    {
        return createdAt;
    }
    
    public long getId()
    {
        return id;
    }
    
    public Long getModifiedAt()
    {
        return modifiedAt;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getObjectId()
    {
        return objectId;
    }
    
    public long getOwnedBy()
    {
        return ownedBy;
    }
    
    public long getParentId()
    {
        return parentId;
    }
    
    public String getSha1()
    {
        return sha1;
    }
    
    public long getSize()
    {
        return size;
    }
    
    public static INodeMetadata convertToMetaNode(String inodeString)
    {
        if (null == inodeString)
        {
            return null;
        }
        INodeMetadata node = new INodeMetadata();
        String[] fieldArrays = inodeString.split("/");
        if (fieldArrays.length != 14)
        {
            throw new InternalServerErrorException("Can get the right field array. lenth is "
                + fieldArrays.length);
        }
        try
        {
            node.setId(Long.parseLong(StringUtils.trimToEmpty(fieldArrays[0])));
            node.setParentId(Long.parseLong(StringUtils.trimToEmpty(fieldArrays[1])));
            node.setType(Byte.parseByte(StringUtils.trimToEmpty(fieldArrays[2])));
            node.setStatus(Byte.parseByte(StringUtils.trimToEmpty(fieldArrays[3])));
            node.setName(fieldArrays[4]);
            node.setSize(Long.parseLong(StringUtils.trimToEmpty(fieldArrays[5])));
            if (!isBlankStr(fieldArrays[6]))
            {
                node.setSha1(fieldArrays[6]);
            }
            if (!isBlankStr(fieldArrays[7]))
            {
                node.setObjectId(fieldArrays[7]);
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
            if (!isBlankStr(fieldArrays[8]))
            {
                node.setModifiedAt(dateFormat.parse(fieldArrays[8]).getTime());
            }
            if (!isBlankStr(fieldArrays[9]))
            {
                node.setModifiedAt(dateFormat.parse(fieldArrays[9]).getTime());
            }
            if (!isBlankStr(fieldArrays[10]))
            {
                node.setModifiedAt(dateFormat.parse(fieldArrays[10]).getTime());
            }
            if (!isBlankStr(fieldArrays[11]))
            {
                node.setModifiedAt(dateFormat.parse(fieldArrays[11]).getTime());
            }
            node.setSyncStatus(Byte.parseByte(StringUtils.trimToEmpty(fieldArrays[12])));
            node.setSyncVersion(Long.parseLong(StringUtils.trimToEmpty(fieldArrays[13])));
            return node;
        }
        catch (NumberFormatException e)
        {
            throw new InternalServerErrorException("Can get the right field array. ", e);
        }
        catch (ParseException e)
        {
            throw new InternalServerErrorException("Can get the right date field array.", e);
        }
    }
    
    private static boolean isBlankStr(String input)
    {
        if (StringUtils.isBlank(input))
        {
            return true;
        }
        String tempString = StringUtils.trimToEmpty(input).toLowerCase(Locale.getDefault());
        
        if ("null".equals(tempString))
        {
            return true;
        }
        if (getNullExpression().equals(tempString))
        {
            return true;
        }
        return false;
    }
    
    
    private static String getNullExpression()
    {
        
        return PropertiesUtils.getProperty("dbtempfile.null.expression", "/" + System.lineSeparator(), PropertiesUtils.BundleName.BRIDGE);
    }
    
    
    public byte getStatus()
    {
        return status;
    }
    
    public int getSyncStatus()
    {
        return syncStatus;
    }
    
    public long getSyncVersion()
    {
        return syncVersion;
    }
    
    public byte getType()
    {
        return type;
    }
    
    public void setContentCreatedAt(Long contentCreatedAt)
    {
        this.contentCreatedAt = contentCreatedAt;
    }
    
    public void setContentModifiedAt(Long contentModifiedAt)
    {
        this.contentModifiedAt = contentModifiedAt;
    }
    
    public void setCreatedAt(Long createdAt)
    {
        this.createdAt = createdAt;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public void setModifiedAt(Long modifiedAt)
    {
        this.modifiedAt = modifiedAt;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setObjectId(String objectId)
    {
        this.objectId = objectId;
    }
    
    public void setOwnedBy(long ownedBy)
    {
        this.ownedBy = ownedBy;
    }
    
    public void setParentId(long parentId)
    {
        this.parentId = parentId;
    }
    
    public void setSha1(String sha1)
    {
        this.sha1 = sha1;
    }
    
    public void setSize(long size)
    {
        this.size = size;
    }
    
    public void setStatus(byte status)
    {
        this.status = status;
    }
    
    public void setSyncStatus(int syncStatus)
    {
        this.syncStatus = syncStatus;
    }
    
    public void setSyncVersion(long syncVersion)
    {
        this.syncVersion = syncVersion;
    }
    
    public void setType(byte type)
    {
        this.type = type;
    }
    
}
