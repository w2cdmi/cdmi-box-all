package pw.cdmi.box.disk.client.domain.message;


/**
 * 
 * @version CloudStor CSE Service Platform Subproject, 2015-3-11
 * @see
 * @since
 */
public class Message extends BasicMessage
{
    private String params;

    private byte status;
    
    private int tableSuffix;
    
    private byte type;

    public String getParams()
    {
        return params;
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

    public void setParams(String params)
    {
        this.params = params;
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
    
    
    
}
