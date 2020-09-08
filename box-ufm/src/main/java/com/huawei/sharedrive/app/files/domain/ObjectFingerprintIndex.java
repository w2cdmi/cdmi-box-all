package com.huawei.sharedrive.app.files.domain;

/**
 * 对象指纹索引对象，通过指纹找到对应的Object
 * 
 * @author c00110381
 * 
 */
public class ObjectFingerprintIndex
{
    /** 对象ID */
    private String id;
    
    /** 区域ID */
    private int regionId;
    
    /** 对象sha1值 */
    private String sha1;
    
    private int tableSuffix;
    
    public ObjectFingerprintIndex()
    {
        
    }
    
    public ObjectFingerprintIndex(ObjectReference input, int regionId)
    {
        this.id = input.getId();
        this.sha1 = input.getSha1();
        this.regionId = regionId;
    }
    
    
    public ObjectFingerprintIndex(String id, String sha1, int regionId)
    {
        this.id = id;
        this.sha1 = sha1;
        this.regionId = regionId;
    }
    
    public String getId()
    {
        return id;
    }
    
    public int getRegionId()
    {
        return regionId;
    }
    
    public String getSha1()
    {
        return sha1;
    }
    
    public int getTableSuffix()
    {
        return tableSuffix;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public void setRegionId(int regionid)
    {
        this.regionId = regionid;
    }
    
    public void setSha1(String sha1)
    {
        this.sha1 = sha1;
    }
    
    public void setTableSuffix(int tableSuffix)
    {
        this.tableSuffix = tableSuffix;
    }

    
}
