package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class InvalidSpaceStatusException extends BaseRunException
{
    private static final long serialVersionUID = 1672779103350226151L;

    public InvalidSpaceStatusException()
    {
        super(HttpStatus.FORBIDDEN, ErrorCode.INVALID_SPACE_STATUS.getCode(), ErrorCode.INVALID_SPACE_STATUS.getMessage());
    }
    
    public InvalidSpaceStatusException(String excepMessage)
    {
        super(HttpStatus.FORBIDDEN, ErrorCode.INVALID_SPACE_STATUS.getCode(), ErrorCode.INVALID_SPACE_STATUS.getMessage(),
            excepMessage);
    }
}
