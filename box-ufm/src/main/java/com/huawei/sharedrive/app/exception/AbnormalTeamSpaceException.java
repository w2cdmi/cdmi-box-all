package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class AbnormalTeamSpaceException extends BaseRunException
{
    private static final long serialVersionUID = 903085371470198712L;

    public AbnormalTeamSpaceException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.ABNORMAL_TEAMSPACE_STATUS.getCode(),
            ErrorCode.ABNORMAL_TEAMSPACE_STATUS.getMessage());
    }
    
    public AbnormalTeamSpaceException(String excepMessage)
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.ABNORMAL_TEAMSPACE_STATUS.getCode(),
            ErrorCode.ABNORMAL_TEAMSPACE_STATUS.getMessage(), excepMessage);
    }
    
    public AbnormalTeamSpaceException(Throwable e)
    {
        super(e, HttpStatus.NOT_FOUND, ErrorCode.ABNORMAL_TEAMSPACE_STATUS.getCode(),
            ErrorCode.ABNORMAL_TEAMSPACE_STATUS.getMessage());
    }
}
