package com.huawei.sharedrive.app.openapi.domain.node;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.exception.BadRequestException;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

public class RestFilesRenameRequest
{
    private String name;
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name) throws BaseRunException
    {
        if (StringUtils.isNotBlank(name))
        {
            FilesCommonUtils.checkNodeNameVaild(name);
            this.name = name;
        }
        else
        {
            throw new BadRequestException();
        }
    }
    
}
