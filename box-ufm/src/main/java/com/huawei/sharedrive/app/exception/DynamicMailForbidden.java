package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class DynamicMailForbidden extends BaseRunException
{
    
    private static final long serialVersionUID = 2294022749211059949L;
    
    public DynamicMailForbidden()
    {
        super(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_LINK_MAIL_OPER.getCode(), ErrorCode.FORBIDDEN_LINK_MAIL_OPER.getMessage());
    }
    
    public DynamicMailForbidden(String excepMessage)
    {
        super(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_LINK_MAIL_OPER.getCode(), ErrorCode.FORBIDDEN_LINK_MAIL_OPER.getMessage(),excepMessage);
    }
    
    public DynamicMailForbidden(Throwable e)
    {
        
        super(e, HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_LINK_MAIL_OPER.getCode(),
            ErrorCode.FORBIDDEN_LINK_MAIL_OPER.getMessage());
        
    }
    
}
