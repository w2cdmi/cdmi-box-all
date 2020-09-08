package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class MobileChangeConflictException extends BaseRunException {

	private static final long serialVersionUID = 6692410086526456234L;

	public MobileChangeConflictException(String msg)
    {
        super(HttpStatus.CONFLICT, ErrorCode.MOBILE_CHANGE_CONFLICT.getCode(),
            ErrorCode.MOBILE_CHANGE_CONFLICT.getMessage(), msg);
    }
}
