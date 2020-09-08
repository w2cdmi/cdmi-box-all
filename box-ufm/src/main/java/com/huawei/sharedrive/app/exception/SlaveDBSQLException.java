package com.huawei.sharedrive.app.exception;

import org.springframework.dao.DataAccessException;

public class SlaveDBSQLException extends DataAccessException
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public SlaveDBSQLException(String msg)
    {
        super(msg);
        
    }
    
    public SlaveDBSQLException(String msg,Throwable e)
    {
        super(msg,e);
        
    }
    
    public SlaveDBSQLException(Throwable e)
    {
        super(e.getMessage(),e);
        
    }
    
}
