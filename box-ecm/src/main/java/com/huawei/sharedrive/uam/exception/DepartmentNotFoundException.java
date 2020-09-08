package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class DepartmentNotFoundException extends BaseRunException
{
    
    private static final long serialVersionUID = -1662211958750936293L;
    
    public DepartmentNotFoundException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.DEPARTMENT_NOT_FOUND.getCode(), ErrorCode.DEPARTMENT_NOT_FOUND.getMessage());
    }
    
    public DepartmentNotFoundException(String msg)
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.DEPARTMENT_NOT_FOUND.getCode(),
            ErrorCode.DEPARTMENT_NOT_FOUND.getMessage(), msg);
    }
    
}
