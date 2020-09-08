package com.huawei.sharedrive.app.openapi.domain.node;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

public class GetNodeByNameRequest
{
    private String name;
    
    public GetNodeByNameRequest()
    {
    }
    
    public GetNodeByNameRequest(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        if(StringUtils.isNotBlank(name))
        {
            name = name.trim();
        }
        this.name = name;
    }
    
    public void checkParameter() throws InvalidParamException
    {
        FilesCommonUtils.checkNodeNameVaild(name);
    }
}
