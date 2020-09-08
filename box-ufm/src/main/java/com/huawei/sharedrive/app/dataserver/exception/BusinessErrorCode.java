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
package com.huawei.sharedrive.app.dataserver.exception;

/**
 * 
 * @author s90006125
 * 
 */
public enum BusinessErrorCode
{
    /** 请请求的对象已经存在 */
    AlreadyExistException(409),
    /** 错误的请求：参数错误 */
    BadRequestException(400), INTERNAL_SERVER_ERROR(500),
    /** 参数缺失 */
    MissingParameterException(400),
    /** 不支持的操作 */
    NotAcceptableException(406),
    /** 请请求的对象不存在 */
    NotFoundException(404),
    
    /** 前置条件不满足 */
    PreconditionFailedException(412);
    
    private int code;
    
    private BusinessErrorCode(int code)
    {
        this.code = code;
    }
    
    public int getCode()
    {
        return code;
    }
}
