package com.huawei.sharedrive.uam.openapi.domain.user;

import java.util.List;

import com.huawei.sharedrive.uam.openapi.domain.UserOrder;

public class RequestSearchUser
{
    
    public static final String TYPE_AD_USER = "aduser";
    
    public static final String TYPE_SYSTEM = "system";
    
    public static final String TYPE_AUTO = "auto";
    
    private String type;
    
    private String appId;
    
    private String keyword;
    
    private Integer limit;
    
    private Integer offset;
    
    private List<UserOrder> terminalOrder;
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public String getAppId()
    {
        return appId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public String getKeyword()
    {
        return keyword;
    }
    
    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }
    
    public Integer getLimit()
    {
        return limit;
    }
    
    public void setLimit(Integer limit)
    {
        this.limit = limit;
    }
    
    public Integer getOffset()
    {
        return offset;
    }
    
    public void setOffset(Integer offset)
    {
        this.offset = offset;
    }
    
    public List<UserOrder> getTerminalOrder()
    {
        return terminalOrder;
    }
    
    public void setTerminalOrder(List<UserOrder> terminalOrder)
    {
        this.terminalOrder = terminalOrder;
    }
    
}
