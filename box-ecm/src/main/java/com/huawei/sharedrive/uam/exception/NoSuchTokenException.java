package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class NoSuchTokenException extends BaseRunException {
    private static final long serialVersionUID = 6347109602323419673L;

    public NoSuchTokenException() {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_TOKEN.getCode(), ErrorCode.NO_SUCH_TOKEN.getMessage());
    }

    public NoSuchTokenException(String error) {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_TOKEN.getCode(), ErrorCode.NO_SUCH_TOKEN.getMessage(), error);
    }
}
