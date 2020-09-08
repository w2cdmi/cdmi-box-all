package com.huawei.sharedrive.cloudapp.cmb.user.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResponseMultiUserSearch implements Serializable
{
    private static final long serialVersionUID = 2695503836083236867L;
    
    private boolean single;
    
    private List<String> failList = new ArrayList<String>();
    
    private List<RetrieveUser> successList = new ArrayList<RetrieveUser>();
    
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
    
    public List<RetrieveUser> getSuccessList()
    {
        return successList;
    }
    
    public void setSuccessList(List<RetrieveUser> successList)
    {
        this.successList = successList;
    }
    
}
