package com.huawei.sharedrive.app.openapi.domain.node;

import com.huawei.sharedrive.app.exception.ExceptionResponseEntity;

public class FilePreUploadFailResponse
{
    private String name;
    
    private ExceptionResponseEntity exception;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public ExceptionResponseEntity getException()
    {
        return exception;
    }

    public void setException(ExceptionResponseEntity exception)
    {
        this.exception = exception;
    }
    
    
}
