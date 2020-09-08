package com.huawei.sharedrive.app.openapi.domain.link;

import java.io.Serializable;
import java.util.List;

import com.huawei.sharedrive.app.share.domain.INodeLinkApprove;

public class RestLinkApproveList implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int limit;
    
    private long offset;
    
    private int totalCount;
    
    List<INodeLinkApprove> linkApproveList;

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public List<INodeLinkApprove> getLinkApproveList() {
		return linkApproveList;
	}

	public void setLinkApproveList(List<INodeLinkApprove> linkApproveList) {
		this.linkApproveList = linkApproveList;
	}
    
    
}
