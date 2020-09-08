package com.huawei.sharedrive.app.mirror.datamigration.exception;

import org.springframework.http.HttpStatus;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.ErrorCode;

public class NoSuchMigrationTaskException extends BaseRunException
{
    private static final long serialVersionUID = 4773393709030753428L;
    
    public NoSuchMigrationTaskException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_USER_MIGRATION_TASK.getCode(),
            ErrorCode.NO_SUCH_USER_MIGRATION_TASK.getMessage());
    }
    
    public NoSuchMigrationTaskException(String excepMessage)
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_USER_MIGRATION_TASK.getCode(),
            ErrorCode.NO_SUCH_USER_MIGRATION_TASK.getMessage(), excepMessage);
    }
    
    public NoSuchMigrationTaskException(Throwable e)
    {
        super(e, HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_USER_MIGRATION_TASK.getCode(),
            ErrorCode.NO_SUCH_USER_MIGRATION_TASK.getMessage());
    }
}
