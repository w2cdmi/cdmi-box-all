package com.huawei.sharedrive.app.openapi.domain.node;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 查询预览图片地址响应
 * 
 * @author Administrator
 *
 */
public class PreviewResponse implements Serializable {

	private static final long serialVersionUID = -7024673822364667816L;

	/**
	 * 预览图片地址url
	 */
	private String url;
	
    /**
     * 返回码
     */
    private String resultCode = "00000000";

	public PreviewResponse(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getResultCode()
    {
        return resultCode;
    }

    public void setResultCode(String resultCode)
    {
        this.resultCode = resultCode;
    }

    @Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
