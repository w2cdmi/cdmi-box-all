package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class ExistShortcutException extends BaseRunException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5179124264667527570L;
	 public ExistShortcutException()
	    {
	        super(HttpStatus.CONFLICT, ErrorCode.EXSIT_SHORTCUT.getCode(), ErrorCode.EXSIT_SHORTCUT.getMessage());
	    }
	    
}
