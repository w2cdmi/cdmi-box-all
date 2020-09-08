package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

/**
 * 异步任务冲突异常
 * <br/>
 * 复制粘贴时，存在重名冲突
 * 还原回收站时，父文件夹已经被删除
 * 
 * @author l90003768
 *
 */
public class AsyncNodesConflictException extends BaseRunException
{
    /**
     * 序列化号
     */
    private static final long serialVersionUID = 7976173906202442667L;

    public AsyncNodesConflictException()
    {
        super(HttpStatus.CONFLICT, ErrorCode.ASYNC_NODES_CONFLICT.getCode(), 
            ErrorCode.ASYNC_NODES_CONFLICT.getMessage());
    }
    
    public AsyncNodesConflictException(String excepMessage)
    {
        super(HttpStatus.CONFLICT, ErrorCode.ASYNC_NODES_CONFLICT.getCode(), 
            ErrorCode.ASYNC_NODES_CONFLICT.getMessage(), excepMessage);
    }
    
    public AsyncNodesConflictException(Throwable e)
    {
        super(e, HttpStatus.CONFLICT, ErrorCode.ASYNC_NODES_CONFLICT.getCode(),
            ErrorCode.ASYNC_NODES_CONFLICT.getMessage());
        
    }
    
}
