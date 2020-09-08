package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class ExistDeclarationException extends BaseRunException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public ExistDeclarationException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_DECLARATION.getCode(),
            ErrorCode.NO_SUCH_DECLARATION.getMessage());
    }
    
}
