package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class PreviewNotSupportedException extends BaseRunException {
    public PreviewNotSupportedException() {
        super(HttpStatus.FORBIDDEN, ErrorCode.PREVIEW_NOT_SUPPORTED.getCode(), ErrorCode.PREVIEW_NOT_SUPPORTED.getMessage());
    }

    public PreviewNotSupportedException(String msg) {
        super(HttpStatus.FORBIDDEN, ErrorCode.PREVIEW_NOT_SUPPORTED.getCode(), ErrorCode.PREVIEW_NOT_SUPPORTED.getMessage(), msg);
    }
}
