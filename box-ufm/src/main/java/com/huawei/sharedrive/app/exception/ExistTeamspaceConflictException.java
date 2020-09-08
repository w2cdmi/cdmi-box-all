package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class ExistTeamspaceConflictException extends BaseRunException
{
    private static final long serialVersionUID = 4773393709030753428L;
    
    public ExistTeamspaceConflictException()
    {
        super(HttpStatus.CONFLICT, ErrorCode.EXIST_TEAMSPACE_CONFLICT.getCode(),
            ErrorCode.EXIST_TEAMSPACE_CONFLICT.getMessage());
    }
    
    public ExistTeamspaceConflictException(String excepMessage)
    {
        super(HttpStatus.CONFLICT, ErrorCode.EXIST_TEAMSPACE_CONFLICT.getCode(),
            ErrorCode.EXIST_TEAMSPACE_CONFLICT.getMessage(), excepMessage);
    }
    
    public ExistTeamspaceConflictException(Throwable e)
    {
        super(e, HttpStatus.CONFLICT, ErrorCode.EXIST_TEAMSPACE_CONFLICT.getCode(),
            ErrorCode.EXIST_TEAMSPACE_CONFLICT.getMessage());
    }
}
