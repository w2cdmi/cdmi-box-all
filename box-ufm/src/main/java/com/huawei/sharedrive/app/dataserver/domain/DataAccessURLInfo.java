package com.huawei.sharedrive.app.dataserver.domain;

import org.apache.commons.lang.builder.ToStringBuilder;

public class DataAccessURLInfo
{
    private String downloadUrl;
    
    private int resourceGroupId;
    
    private String uploadUrl;
    
 
    private String previewUrl;
    
    public DataAccessURLInfo(int resourceGroupId, String downloadUrl, String uploadUrl)
    {
        this.resourceGroupId = resourceGroupId;
        this.downloadUrl = downloadUrl;
        this.uploadUrl = uploadUrl;
    }

   public DataAccessURLInfo(int resourceGroupId, String downloadUrl, String uploadUrl,String previewUrl)
    {
    	this.resourceGroupId = resourceGroupId;
        this.downloadUrl = downloadUrl;
        this.uploadUrl = uploadUrl;
        this.previewUrl=previewUrl;
    }
    public String getDownloadUrl()
    {
        return downloadUrl;
    }
    
    public int getResourceGroupId()
    {
        return resourceGroupId;
    }
    
    public void setDownloadUrl(String downloadUrl)
    {
        this.downloadUrl = downloadUrl;
    }
    
    public void setResourceGroupId(int resourceGroupId)
    {
        this.resourceGroupId = resourceGroupId;
    }
    
    public String getUploadUrl()
    {
        return uploadUrl;
    }
    
    public void setUploadUrl(String uploadUrl)
    {
        this.uploadUrl = uploadUrl;
    }
	public String getPreviewUrl() {
		return previewUrl;
	}

	public void setPreviewUrl(String previewUrl) {
		this.previewUrl = previewUrl;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
    
}
