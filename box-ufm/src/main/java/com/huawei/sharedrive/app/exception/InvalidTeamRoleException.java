package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class InvalidTeamRoleException extends BaseRunException
{
    private static final long serialVersionUID = -7613937609852343574L;
    
    public InvalidTeamRoleException()
    {
        super(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_TEAMROLE.getCode(),
            ErrorCode.INVALID_TEAMROLE.getMessage());
    }
    
    public InvalidTeamRoleException(String excepMessage)
    {
        super(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_TEAMROLE.getCode(),
            ErrorCode.INVALID_TEAMROLE.getMessage(), excepMessage);
    }
    
    public InvalidTeamRoleException(Throwable e)
    {
        super(e, HttpStatus.BAD_REQUEST, ErrorCode.INVALID_TEAMROLE.getCode(),
            ErrorCode.INVALID_TEAMROLE.getMessage());
    }
}
