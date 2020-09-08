package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

/**
 * 终端禁用
 * 
 * @author h90005572
 * 
 */
public class DisabledTerminalStatusException extends BaseRunException
{
    private static final long serialVersionUID = -2147093566523637264L;
    
    public DisabledTerminalStatusException()
    {
        super(HttpStatus.UNAUTHORIZED, ErrorCode.CLIENTUNAUTHORIZED.getCode(),
            ErrorCode.CLIENTUNAUTHORIZED.getMessage());
    }
    
    public DisabledTerminalStatusException(String excepMessage)
    {
        super(HttpStatus.UNAUTHORIZED, ErrorCode.CLIENTUNAUTHORIZED.getCode(),
            ErrorCode.CLIENTUNAUTHORIZED.getMessage(), excepMessage);
    }
}
