package com.huawei.sharedrive.uam.httpclient.rest.response;

import org.apache.http.HttpStatus;
import org.springframework.util.StringUtils;

public class HttpResult
{
    private int httpStatus = HttpStatus.SC_BAD_REQUEST;
    
    private String responseBody;
    
    public boolean isEmpty()
    {
        return StringUtils.isEmpty(responseBody);
    }
    
    public HttpResult()
    {
        super();
    }
    
    public HttpResult(int httpStatus)
    {
        super();
        this.httpStatus = httpStatus;
    }
    
    public int getHttpStatus()
    {
        return httpStatus;
    }
    
    public void setHttpStatus(int httpStatus)
    {
        this.httpStatus = httpStatus;
    }
    
    public String getResponseBody()
    {
        return responseBody;
    }
    
    public void setResponseBody(String responseBody)
    {
        this.responseBody = responseBody;
    }
    
}
