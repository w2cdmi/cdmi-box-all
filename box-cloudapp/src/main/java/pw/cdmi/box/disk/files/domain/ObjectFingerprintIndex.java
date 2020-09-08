package pw.cdmi.box.disk.files.domain;

public class ObjectFingerprintIndex
{
    private String id;
    
    private int regionId;
    
    private String sha1;
    
    private int tableSuffix;
    
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
