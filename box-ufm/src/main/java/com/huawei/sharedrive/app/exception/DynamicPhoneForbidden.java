package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class DynamicPhoneForbidden extends BaseRunException
{
    
    private static final long serialVersionUID = 2294022749211059949L;
    
    public DynamicPhoneForbidden()
    {
        super(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_LINK_PHONE_OPER.getCode(), ErrorCode.FORBIDDEN_LINK_PHONE_OPER.getMessage());
    }
    
    public DynamicPhoneForbidden(String excepMessage)
    {
        super(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_LINK_PHONE_OPER.getCode(), ErrorCode.FORBIDDEN_LINK_PHONE_OPER.getMessage(),excepMessage);
    }
    
    public DynamicPhoneForbidden(Throwable e)
    {
        
        super(e, HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_LINK_PHONE_OPER.getCode(),
            ErrorCode.FORBIDDEN_LINK_PHONE_OPER.getMessage());
        
    }
    
}
