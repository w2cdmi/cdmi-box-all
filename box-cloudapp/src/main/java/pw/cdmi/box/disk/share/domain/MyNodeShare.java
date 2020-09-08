package pw.cdmi.box.disk.share.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pw.cdmi.box.disk.client.domain.node.ThumbnailUrl;

public class MyNodeShare
{
    public static final byte STATUS_IN_RECYCLE = 1;
    
    public static final byte STATUS_NORMAL = 0;
    
    public static final byte TYPE_FILE = 1;
    
    public static final byte TYPE_FOLDER = 0;
    
    private long nodeId;
    
    private String name;
    
    private long ownerId;
    
    private String ownerLoginName;
    
    private String ownerName;
    
    private String roleName;
    
    private long size;
    
    private byte status;
    
    @JsonIgnore
    private int tableSuffix;
    
    private List<ThumbnailUrl> thumbnailUrlList;
    
    private byte type;
    
    public String getName()
    {
        return name;
    }
    
    public long getOwnerId()
    {
        return ownerId;
    }
    
    public String getOwnerLoginName()
    {
        return ownerLoginName;
    }
    
    public String getOwnerName()
    {
        return ownerName;
    }
    
    public String getRoleName()
    {
        return roleName;
    }
    
    public long getSize()
    {
        return size;
    }
    
    public byte getStatus()
    {
        return status;
    }
    
    public int getTableSuffix()
    {
        return tableSuffix;
    }
    
    public byte getType()
    {
        return type;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setOwnerId(long ownerId)
    {
        this.ownerId = ownerId;
    }
    
    public void setOwnerLoginName(String ownerLoginName)
    {
        this.ownerLoginName = ownerLoginName;
    }
    
    public void setOwnerName(String ownerName)
    {
        this.ownerName = ownerName;
    }
    
    public void setRoleName(String roleName)
    {
        this.roleName = roleName;
    }
    
    public void setSize(long size)
    {
        this.size = size;
    }
    
    public void setStatus(byte status)
    {
        this.status = status;
    }
    
    public void setTableSuffix(int tableSuffix)
    {
        this.tableSuffix = tableSuffix;
    }
    
    public void setType(byte type)
    {
        this.type = type;
    }
    
    public long getNodeId()
    {
        return nodeId;
    }
    
    public void setNodeId(long nodeId)
    {
        this.nodeId = nodeId;
    }
    
    public List<ThumbnailUrl> getThumbnailUrlList()
    {
        return thumbnailUrlList;
    }
    
    public void setThumbnailUrlList(List<ThumbnailUrl> thumbnailUrlList)
    {
        this.thumbnailUrlList = thumbnailUrlList;
    }
    
}
