package pw.cdmi.box.disk.httpclient.rest.response;

import java.util.List;
import java.util.Map;

public class ControlUserResponse
{
    private boolean result;
    
    private int size;
    
    private Map<String, List<ControlUser>> map;
    
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
    
    public Map<String, List<ControlUser>> getMap()
    {
        return map;
    }
    
    public void setMap(Map<String, List<ControlUser>> map)
    {
        this.map = map;
    }
}
