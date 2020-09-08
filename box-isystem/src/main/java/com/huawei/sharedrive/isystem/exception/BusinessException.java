/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package com.huawei.sharedrive.isystem.exception;

/**
 * 
 * @author s90006125
 * 
 */
public class BusinessException extends RuntimeException
{
    private static final long serialVersionUID = -7738340645030834742L;
    
    private int code;
    
    private String message;
    
    public BusinessException(BusinessErrorCode code, String message)
    {
        this(message);
        this.code = code.getCode();
        this.message = message;
    }
    
    public BusinessException(String message,Exception e)
    {
       super(message, e);
       this.message = message;
    }
    
    public BusinessException(int code, String message)
    {
        this(message);
        this.code = code;
        this.message = message;
    }
    
    public BusinessException()
    {
        super();
        this.code = BusinessErrorCode.INTERNAL_SERVER_ERROR.getCode();
    }
    
    public BusinessException(Throwable cause)
    {
        super(cause);
        this.code = BusinessErrorCode.INTERNAL_SERVER_ERROR.getCode();
    }
    
    public BusinessException(String message)
    {
        super(message);
        this.code = BusinessErrorCode.INTERNAL_SERVER_ERROR.getCode();
    }
    
    public int getCode()
    {
        return code;
    }
    
    public void setCode(int code)
    {
        this.code = code;
    }
    
    public String getMessage()
    {
        return message;
    }
    
    public void setMessage(String message)
    {
        this.message = message;
    }
}
