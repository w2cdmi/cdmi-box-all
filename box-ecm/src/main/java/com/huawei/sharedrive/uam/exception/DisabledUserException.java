package com.huawei.sharedrive.uam.exception;

import org.apache.shiro.authc.AuthenticationException;

public class DisabledUserException extends AuthenticationException {

    private static final long serialVersionUID = 7618054262326234126L;

    public DisabledUserException() {
        super();
    }

    public DisabledUserException(String error) {
        super(error);
    }

    public DisabledUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
