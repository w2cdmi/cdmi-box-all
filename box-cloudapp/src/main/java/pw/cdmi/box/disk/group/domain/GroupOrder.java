package pw.cdmi.box.disk.group.domain;

public class GroupOrder
{
    private String direction;
    
    private String field;
    
    public GroupOrder()
    {
        
    }
    
    public GroupOrder(String field, String direction)
    {
        this.field = field;
        this.direction = direction;
    }
    
    public String getDirection()
    {
        return direction;
    }
    
    public String getField()
    {
        return field;
    }
    
    public void setDirection(String direction)
    {
        this.direction = direction;
    }
    
    public void setField(String field)
    {
        this.field = field;
    }
    
}
