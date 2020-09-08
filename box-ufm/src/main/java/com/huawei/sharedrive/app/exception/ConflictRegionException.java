package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

/**
 * 用户存储区域冲突异常
 * 
 * @author l90003768
 *
 */
public class ConflictRegionException extends BaseRunException
{
    private static final long serialVersionUID = -1551040905880565493L;

    public ConflictRegionException()
    {
        super(HttpStatus.CONFLICT, ErrorCode.CONFLICT_REGION.getCode(), ErrorCode.CONFLICT_REGION.getMessage());
    }
    
}
