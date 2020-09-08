package pw.cdmi.box.disk.httpclient.rest.response;

import java.util.ArrayList;
import java.util.List;

import pw.cdmi.box.disk.share.domain.RestMultiUser;

public class MultiUserSearchResponse
{
    private boolean single;
    
    private static final int INITIAL_SIZE = 10;
    
    private List<String> failList = new ArrayList<String>(INITIAL_SIZE);
    
    private List<RestMultiUser> successList = new ArrayList<RestMultiUser>(INITIAL_SIZE);
    
    public boolean isSingle()
    {
        return single;
    }
    
    public void setSingle(boolean single)
    {
        this.single = single;
    }
    
    public List<String> getFailList()
    {
        return failList;
    }
    
    public void setFailList(List<String> failList)
    {
        this.failList = failList;
    }
    
    public List<RestMultiUser> getSuccessList()
    {
        return successList;
    }
    
    public void setSuccessList(List<RestMultiUser> successList)
    {
        this.successList = successList;
    }
    
}
