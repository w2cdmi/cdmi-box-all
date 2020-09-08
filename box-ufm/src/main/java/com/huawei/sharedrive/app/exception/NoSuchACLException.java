package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class NoSuchACLException extends BaseRunException
{
    private static final long serialVersionUID = -4240060714424823327L;
    
    public NoSuchACLException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_ACL.getCode(), ErrorCode.NO_SUCH_ACL.getMessage());
    }
    
    public NoSuchACLException(String excepMessage)
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_ACL.getCode(), ErrorCode.NO_SUCH_ACL.getMessage(),
            excepMessage);
    }
}
