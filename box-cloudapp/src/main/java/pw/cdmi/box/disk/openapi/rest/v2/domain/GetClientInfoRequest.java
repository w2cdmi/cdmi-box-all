package pw.cdmi.box.disk.openapi.rest.v2.domain;

import java.io.Serializable;

import pw.cdmi.core.exception.BaseRunException;
import pw.cdmi.core.exception.InvalidParamException;

public class GetClientInfoRequest implements Serializable
{
    
    private static final long serialVersionUID = 7560308503124480905L;
    
    private String clientType;
    
    public void checkParameter() throws BaseRunException
    {
        for (ClientType type : ClientType.values())
        {
            if (type.getValue().equalsIgnoreCase(clientType))
            {
                return;
            }
        }
        throw new InvalidParamException();
    }
    
    public String getClientType()
    {
        return clientType;
    }
    
    public void setClientType(String clientType)
    {
        this.clientType = clientType;
    }
    
}
