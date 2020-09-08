package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

/**
 * 目标文件夹已经是源文件夹的父文件夹
 * 
 * @author l90003768
 *
 */
public class SameParentConflictException extends BaseRunException
{
    /**
     * 序列化号
     */
    private static final long serialVersionUID = 7976173906202442667L;

    public SameParentConflictException()
    {
        super(HttpStatus.CONFLICT, ErrorCode.SAME_PARENT_CONFILICT.getCode(), ErrorCode.SAME_PARENT_CONFILICT.getMessage());
    }
    
    public SameParentConflictException(String excepMessage)
    {
        super(HttpStatus.CONFLICT, ErrorCode.SAME_PARENT_CONFILICT.getCode(), ErrorCode.SAME_PARENT_CONFILICT.getMessage(), excepMessage);
    }
    
    public SameParentConflictException(Throwable e)
    {
        super(e, HttpStatus.CONFLICT, ErrorCode.SAME_PARENT_CONFILICT.getCode(),
            ErrorCode.SAME_PARENT_CONFILICT.getMessage());
    }
    
}
