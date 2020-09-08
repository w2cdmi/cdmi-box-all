package com.huawei.sharedrive.app.openapi.domain.node;

import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 预览元数据响应
 * 
 * @author d0000056324
 *
 */
public class PreviewMetaResponse implements Serializable {
	private static final Logger LOGGER = LoggerFactory.getLogger(PreviewMetaResponse.class);
	private static final long serialVersionUID = 1336685829221690381L;

	/**
	 * 是否支持预览
	 */
	private boolean previewSupport;

	/**
	 * 预览总页数，当支持预览，但未完成转换任务时该字段为空
	 */
	private int totalPages = -1;

	/**
	 * 预览rang信息集合，当支持预览，但未完成转换任务时该字段为空 格式：1:0-100,2:101-205……
	 */
	private String range = "";
	
	/**
	 * 返回码
	 */
	private String resultCode = "00000000";

	/**
	 * size //放置原始文件大小，在轉換時會對原始文件大小做限制。配置為convert.properties的convert.task.bigFileSize，【如果以后優化修改到數據庫配置，請修改這里的注釋】
	 * @throws
	 */
	private long inodeSize = 0; 
	
	/**
	 * 注意它的get方法
	 * 為convert.properties的convert.task.bigFileSize的值，【如果以后優化修改到數據庫配置，請修改這里的注釋及它的get方法】
	 * @throws
	 */
	private long maxSize = 0;
	
	public PreviewMetaResponse () {
		Properties preperties = new Properties();
		try {
	        preperties.load((PreviewMetaResponse.class.getResourceAsStream("/convert.properties")));
        } catch (IOException e) {
        	LOGGER.error("load convert.properties! check is file !",e);
        }
		maxSize = Integer.valueOf(preperties.getProperty("convert.task.bigFileSize","10485760").trim());
	}
	
	public boolean getPreviewSupport() {
		return previewSupport;
	}

	public void setPreviewSupport(boolean previewSupport) {
		this.previewSupport = previewSupport;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
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

	public long getInodeSize() {
		return inodeSize;
	}

	public void setInodeSize(long inodeSize) {
		this.inodeSize = inodeSize;
	}

	public long getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(long maxSize) {
		this.maxSize = maxSize;
	}

}
