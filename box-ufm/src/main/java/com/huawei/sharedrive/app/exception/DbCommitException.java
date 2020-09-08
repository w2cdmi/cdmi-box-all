package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

/**
 * 数据库事务提交异常
 * 
 * @author l90003768
 * 
 */
public class DbCommitException extends BaseRunException
{
    private static final long serialVersionUID = 4200186677249802080L;
    
    public DbCommitException()
    {
        super(HttpStatus.EXPECTATION_FAILED, "TransactionCommitError",
            ErrorCode.DB_SUBMIT_EXCEPTION.toString());
    }
    
    public DbCommitException(Throwable e)
    {
        super(HttpStatus.EXPECTATION_FAILED, "TransactionCommitError",
            ErrorCode.DB_SUBMIT_EXCEPTION.toString(), e);
    }
    
    public DbCommitException(String excepMessage)
    {
        super(HttpStatus.EXPECTATION_FAILED, "TransactionCommitError",
            ErrorCode.DB_SUBMIT_EXCEPTION.toString(),excepMessage);
    }
    
    
}
