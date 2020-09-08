package com.huawei.sharedrive.app.openapi.domain.node;

/**
 * 文件预览响应对象
 * @author l90003768
 *
 */
public class SwfDownloadResponse
{
    private String swfUrl;
    
    public SwfDownloadResponse()
    {
        
    }

    public SwfDownloadResponse(String swfUrl)
    {
        this.swfUrl = swfUrl;
    }
    
    public String getSwfUrl()
    {
        return swfUrl;
    }
    public void setSwfUrl(String swfUrl)
    {
        this.swfUrl = swfUrl;
    }
    
    
}
