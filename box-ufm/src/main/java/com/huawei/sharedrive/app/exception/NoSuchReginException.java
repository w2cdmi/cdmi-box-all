package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

/**
 * 存储区域不存在
 * @author l90003768
 *
 */
public class NoSuchReginException extends BaseRunException
{
    private static final long serialVersionUID = -1551040905880565493L;
    
    /**
     * 存储区域不存在
     */
    public NoSuchReginException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_REGION.getCode(), ErrorCode.NO_SUCH_REGION.getMessage());
    }
    
}
