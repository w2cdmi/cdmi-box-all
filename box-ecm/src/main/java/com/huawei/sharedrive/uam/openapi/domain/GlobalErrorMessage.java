/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.openapi.domain;

/************************************************************
 * @Description:
 * <pre>定义系统中的全局错误码</pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/11/27
 ************************************************************/
public enum GlobalErrorMessage {
    OK(0, "OK"),
    FAIL(-1, "failed."),
    USER_NOT_EXIST(10000, "User doesn't exist."),
    ACCOUNT_NOT_EXIST(10001, "Account doesn't exist."),
    TOO_MANY_ACCOUNT(10002, "Too many account."),
    USERNAME_PASSWORD_WRONG(10003, "Username or password is wrong."),
    USER_LOCKED(10004, "User is locked."),
    USER_DISABLED(10005, "User is disabled."),
    LDAP_AUTH_FAILED(10006, "LDAP authentication failed."),
    SECURITY_MATRIX_FAILED(10007, "Security matrix check failed."),
    ACCOUNT_EXIST(10008, "Account has existed.");

    private int errorCode;
    private String errorMessage;
    private GlobalErrorMessage(int code, String message) {
        this.errorCode = code;
        this.errorMessage = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
