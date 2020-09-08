package com.huawei.sharedrive.app.openapi.domain.share;

/**
 * 保存转发记录请求
 */
public class RestSaveForwardRequestV2 {
    private long forwardId;
    private long toId;


    public long getToId() {
        return toId;
    }

    public void setToId(long toId) {
        this.toId = toId;
    }

	public long getForwardId() {
		return forwardId;
	}

	public void setForwardId(long forwardId) {
		this.forwardId = forwardId;
	}
    
    
}