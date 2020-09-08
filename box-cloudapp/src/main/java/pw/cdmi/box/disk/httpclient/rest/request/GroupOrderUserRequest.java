package pw.cdmi.box.disk.httpclient.rest.request;

public class GroupOrderUserRequest extends GroupOrderRequest
{
    private String listRole;
    
    public String getListRole()
    {
        return listRole;
    }
    
    public void setListRole(String listRole)
    {
        this.listRole = listRole;
    }
    
}
