/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.openapi.domain;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>Rest响应基类, 所有的响应都应该继承该类</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/11/20
 ************************************************************/
public class RestResponse {
    private int errorCode = 0;
    private String errorMessage;

    public RestResponse() {
    }

    public RestResponse(int code) {
        this.errorCode = code;
    }

    public RestResponse(int code, String message) {
        this.errorCode = code;
        this.errorMessage = message;
    }

    public RestResponse(GlobalErrorMessage error) {
        this.errorCode = error.getErrorCode();
        this.errorMessage = error.getErrorMessage();
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
