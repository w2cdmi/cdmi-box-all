package pw.cdmi.box.disk.httpclient.rest.response;

import java.util.List;
import java.util.Map;

import pw.cdmi.box.disk.user.domain.User;

public class CollectUserResponse
{
    private boolean result;
    
    private int size;
    
    private Map<String, List<User>> map;
    
    public boolean isResult()
    {
        return result;
    }
    
    public void setResult(boolean result)
    {
        this.result = result;
    }
    
    public int getSize()
    {
        return size;
    }
    
    public void setSize(int size)
    {
        this.size = size;
    }
    
    public Map<String, List<User>> getMap()
    {
        return map;
    }
    
    public void setMap(Map<String, List<User>> map)
    {
        this.map = map;
    }
}
