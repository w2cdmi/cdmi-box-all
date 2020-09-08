package com.huawei.sharedrive.app.mirror.datamigration.exception;

import org.springframework.http.HttpStatus;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.ErrorCode;

public class TaskConflictException extends BaseRunException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 671460002067226370L;
    
    public TaskConflictException()
    {
        super(HttpStatus.CONFLICT, ErrorCode.EXIS_USER_MIGRATION_TASK.getCode(),
            ErrorCode.EXIS_USER_MIGRATION_TASK.getMessage());
    }
    
    public TaskConflictException(String excepMessage)
    {
        super(HttpStatus.CONFLICT, ErrorCode.EXIS_USER_MIGRATION_TASK.getCode(),
            ErrorCode.EXIS_USER_MIGRATION_TASK.getMessage(), excepMessage);
    }
    
    public TaskConflictException(Throwable e)
    {
        super(e, HttpStatus.CONFLICT, ErrorCode.EXIS_USER_MIGRATION_TASK.getCode(),
            ErrorCode.EXIS_USER_MIGRATION_TASK.getMessage());
    }
}
