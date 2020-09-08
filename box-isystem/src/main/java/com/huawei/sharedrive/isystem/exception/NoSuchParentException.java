package com.huawei.sharedrive.isystem.exception;

import org.springframework.http.HttpStatus;

public class NoSuchParentException extends BaseRunException
{
    
    private static final long serialVersionUID = -4397027085460048774L;
    
    public NoSuchParentException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_FOLDER.getCode(), ErrorCode.NO_SUCH_FOLDER.getMessage());
    }
}
