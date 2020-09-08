package com.huawei.sharedrive.uam.openapi.domain;

public class RestWxLoginResponse extends RestLoginResponse {
    EnterpriseInfo[] enterpriseList;

    public RestWxLoginResponse() {
    }

    public RestWxLoginResponse(GlobalErrorMessage error) {
        super(error);
    }

    public EnterpriseInfo[] getEnterpriseList() {
        return enterpriseList;
    }

    public void setEnterpriseList(EnterpriseInfo[] enterpriseList) {
        this.enterpriseList = enterpriseList;
    }
}
