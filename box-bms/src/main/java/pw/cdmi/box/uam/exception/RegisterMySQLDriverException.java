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
package pw.cdmi.box.uam.exception;

/**
 * 
 * @author s90006125
 *
 */
public class RegisterMySQLDriverException extends RuntimeException
{
    private static final long serialVersionUID = -4428079704083427756L;

    public RegisterMySQLDriverException(String message, Throwable e) 
    {
        super(message, e);
    }
}
