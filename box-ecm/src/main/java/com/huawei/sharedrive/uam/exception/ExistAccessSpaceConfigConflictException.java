package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class ExistAccessSpaceConfigConflictException extends BaseRunException
{
    
    private static final long serialVersionUID = 434722930778933304L;
    
    public ExistAccessSpaceConfigConflictException()
    {
        super(HttpStatus.CONFLICT, ErrorCode.EXIST_SPACESCONFIG_CONFLICT.getCode(),
            ErrorCode.EXIST_SPACESCONFIG_CONFLICT.getMessage());
    }
    
    public ExistAccessSpaceConfigConflictException(String msg)
    {
        super(HttpStatus.CONFLICT, ErrorCode.EXIST_SPACESCONFIG_CONFLICT.getCode(),
            ErrorCode.EXIST_SPACESCONFIG_CONFLICT.getMessage(), msg);
    }
}
