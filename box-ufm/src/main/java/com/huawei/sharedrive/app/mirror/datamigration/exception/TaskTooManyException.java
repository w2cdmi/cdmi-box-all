package com.huawei.sharedrive.app.mirror.datamigration.exception;

import org.springframework.http.HttpStatus;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.ErrorCode;

public class TaskTooManyException extends BaseRunException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 671460002067226370L;
    
    public TaskTooManyException()
    {
        super(HttpStatus.TOO_MANY_REQUESTS, ErrorCode.TOO_MANY_MIGRATION_TASK.getCode(),
            ErrorCode.TOO_MANY_MIGRATION_TASK.getMessage());
    }
    
    public TaskTooManyException(String excepMessage)
    {
        super(HttpStatus.TOO_MANY_REQUESTS, ErrorCode.TOO_MANY_MIGRATION_TASK.getCode(),
            ErrorCode.TOO_MANY_MIGRATION_TASK.getMessage(), excepMessage);
    }
    
    public TaskTooManyException(Throwable e)
    {
        super(e, HttpStatus.TOO_MANY_REQUESTS, ErrorCode.TOO_MANY_MIGRATION_TASK.getCode(),
            ErrorCode.TOO_MANY_MIGRATION_TASK.getMessage());
    }
}
