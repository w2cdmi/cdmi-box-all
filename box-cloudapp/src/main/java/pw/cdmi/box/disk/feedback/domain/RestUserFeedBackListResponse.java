package pw.cdmi.box.disk.feedback.domain;

import java.io.Serializable;
import java.util.List;

public class RestUserFeedBackListResponse implements Serializable{
	
	private static final long serialVersionUID = 4126646000933047201L;

	protected List<RestFeedBackInfo> feedBackInfoList;
	
	private int limit;
    
    private long offset;
    
    private int totalCount;

	public List<RestFeedBackInfo> getFeedBackInfoList() {
		return feedBackInfoList;
	}

	public void setFeedBackInfoList(List<RestFeedBackInfo> feedBackInfoList) {
		this.feedBackInfoList = feedBackInfoList;
	}

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
}
