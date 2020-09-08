package pw.cdmi.box.uam.enterprise.domain;

import java.io.Serializable;

public class OperationType implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private long id;
    
    private String operationName;
    
    public long getId()
    {
        return id;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public String getOperationName()
    {
        return operationName;
    }
    
    public void setOperationName(String operationName)
    {
        this.operationName = operationName;
    }
    
}
