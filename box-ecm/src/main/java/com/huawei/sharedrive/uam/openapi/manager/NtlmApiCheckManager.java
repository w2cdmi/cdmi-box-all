package com.huawei.sharedrive.uam.openapi.manager;

import javax.servlet.http.HttpServletRequest;

import com.huawei.sharedrive.uam.exception.InvalidParamterException;

public interface NtlmApiCheckManager
{
    void checkNtlmParamter(String appId, String key, String challenge, HttpServletRequest request)
        throws InvalidParamterException;
}
