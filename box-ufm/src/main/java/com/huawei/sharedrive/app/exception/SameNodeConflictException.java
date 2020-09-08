package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

/**
 * 源文件夹和目标文件夹是相同的文件夹
 * 
 * @author l90003768
 *
 */
public class SameNodeConflictException extends BaseRunException
{
    /**
     * 序列化号
     */
    private static final long serialVersionUID = 7976173906202442667L;

    public SameNodeConflictException()
    {
        super(HttpStatus.CONFLICT, ErrorCode.SAME_NODE_CONFILICT.getCode(), ErrorCode.SAME_NODE_CONFILICT.getMessage());
    }
    
    public SameNodeConflictException(String excepMessage)
    {
        super(HttpStatus.CONFLICT, ErrorCode.SAME_NODE_CONFILICT.getCode(), ErrorCode.SAME_NODE_CONFILICT.getMessage(), excepMessage);
    }
    
    public SameNodeConflictException(Throwable e)
    {
        super(e, HttpStatus.CONFLICT, ErrorCode.SAME_NODE_CONFILICT.getCode(),
            ErrorCode.SAME_NODE_CONFILICT.getMessage());
    }
    
}
