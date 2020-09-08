package com.huawei.sharedrive.uam.telecom.model;

/**
 * 表示从电信平台获取的用户信息对象
 * 
 * 
 */
public class UserInfo {

	public String streamingNo;

	public String oPFlag;

	public String returnStatus;

	public String summary;

	public String getStreamingNo() {
		return streamingNo;
	}

	public void setStreamingNo(String streamingNo) {
		this.streamingNo = streamingNo;
	}

	public String getoPFlag() {
		return oPFlag;
	}

	public void setoPFlag(String oPFlag) {
		this.oPFlag = oPFlag;
	}

	public String getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(String returnStatus) {
		this.returnStatus = returnStatus;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

}
