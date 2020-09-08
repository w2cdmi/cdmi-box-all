package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class DeclarationException extends BaseRunException
{
    private static final long serialVersionUID = -5694937756187405397L;
    
    public DeclarationException()
    {
        super(HttpStatus.UNAUTHORIZED, ErrorCode.NEEDSIGNDECLARATION.getCode(),
            ErrorCode.NEEDSIGNDECLARATION.getMessage());
    }
    
    public DeclarationException(String msg)
    {
        super(HttpStatus.UNAUTHORIZED, ErrorCode.NEEDSIGNDECLARATION.getCode(),
            ErrorCode.NEEDSIGNDECLARATION.getMessage(), msg);
    }
    
    public DeclarationException(Throwable e)
    {
        
        super(e, HttpStatus.UNAUTHORIZED, ErrorCode.NEEDSIGNDECLARATION.getCode(),
            ErrorCode.NEEDSIGNDECLARATION.getMessage());
        
    }
    
}
