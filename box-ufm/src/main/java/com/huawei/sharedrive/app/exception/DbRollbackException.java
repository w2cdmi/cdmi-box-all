package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

/**
 * 数据库事务回滚异常
 * 
 * @author l90003768
 * 
 */
public class DbRollbackException extends BaseRunException
{
    
    private static final long serialVersionUID = -4564360586972221238L;
    
    public DbRollbackException()
    {
        super(HttpStatus.EXPECTATION_FAILED, "TransactionRollbackError",
            ErrorCode.DB_ROLL_BACK_EXCEPTION.toString());
    }
    
    public DbRollbackException(String excepMessage)
    {
        super(HttpStatus.EXPECTATION_FAILED, "TransactionRollbackError",
            ErrorCode.DB_ROLL_BACK_EXCEPTION.toString(), excepMessage);
    }
    
    public DbRollbackException(String excepMessage, Exception e)
    {
        super(HttpStatus.EXPECTATION_FAILED, "TransactionRollbackError",
            ErrorCode.DB_ROLL_BACK_EXCEPTION.toString(), excepMessage, e);
    }
    
}
