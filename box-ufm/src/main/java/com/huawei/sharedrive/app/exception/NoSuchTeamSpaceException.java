package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class NoSuchTeamSpaceException extends BaseRunException
{
    private static final long serialVersionUID = -5766903180102012235L;

    public NoSuchTeamSpaceException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_TEAMSPACE.getCode(), ErrorCode.NO_SUCH_TEAMSPACE.getMessage());
    }
    
    public NoSuchTeamSpaceException(String excepMessage)
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_TEAMSPACE.getCode(), ErrorCode.NO_SUCH_TEAMSPACE.getMessage(),
            excepMessage);
    }
}
