package pw.cdmi.box.disk.client.domain.node;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.core.exception.InvalidParamException;


public class RefreshUploadUrlRquest
{
    
    private static final int UPLOADURL_SPLIT_MIN_LENGTH = 6;
    
    private String uploadUrl;
    
    public RefreshUploadUrlRquest()
    {
        
    }
    
    public RefreshUploadUrlRquest(String uploadUrl)
    {
        this.uploadUrl = uploadUrl;
    }
    
    public void checkParameter() throws InvalidParamException
    {
        if (StringUtils.isBlank(uploadUrl))
        {
            throw new InvalidParamException();
        }
        String[] array = uploadUrl.split("/");
        if (array.length < UPLOADURL_SPLIT_MIN_LENGTH)
        {
            throw new InvalidParamException();
        }
    }
    
    public String getUploadUrl()
    {
        return uploadUrl;
    }
    
    public void setUploadUrl(String uploadUrl)
    {
        this.uploadUrl = uploadUrl;
    }
    
}
