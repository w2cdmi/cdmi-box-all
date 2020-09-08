package pw.cdmi.box.disk.share.domain;

import java.io.Serializable;

public class SharedUser implements Serializable
{
    private static final long serialVersionUID = -17673849732079913L;
    
    private long id;
    
    private byte type;
    
    public long getId()
    {
        return id;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public byte getType()
    {
        return type;
    }
    
    public void setType(byte type)
    {
        this.type = type;
    }
    
}
