package com.huawei.sharedrive.app.files.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UploadMultipartFileRSP
{
    @JsonProperty(value = "uploadURL")
    private String uploadUrl;
    
    public String getUploadUrl()
    {
        return uploadUrl;
    }
    
    public void setUploadUrl(String uploadUrl)
    {
        this.uploadUrl = uploadUrl;
    }
}
