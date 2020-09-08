package pw.cdmi.box.disk.client.domain.share;

import java.io.Serializable;

import pw.cdmi.box.domain.Order;

public class RestLinkApproveListRequest implements Serializable {
    private static final int DEFAULT_LIMIT = 100;
    private static final long DEFAULT_OFFSET = 0L;

    private Long accountId;
	private Long linkOwner;
	private Long approveBy;
	private Byte type;
	private Byte status;

	//分页、排序相关
    private Integer limit;
    private Long offset;
    private Order order;

    public RestLinkApproveListRequest() {
        limit = DEFAULT_LIMIT;
        offset = DEFAULT_OFFSET;
    }

    public RestLinkApproveListRequest(Integer limit, Long offset) {
        this.limit = limit != null ? limit : DEFAULT_LIMIT;
        this.offset = offset != null ? offset : DEFAULT_OFFSET;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getLinkOwner() {
		return linkOwner;
	}

	public void setLinkOwner(Long linkOwner) {
		this.linkOwner = linkOwner;
	}

	public Long getApproveBy() {
		return approveBy;
	}

	public void setApproveBy(Long approveBy) {
		this.approveBy = approveBy;
	}

	public Byte getStatus() {
		return status;
	}

	public void setStatus(Byte status) {
		this.status = status;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Long getOffset() {
		return offset;
	}

	public void setOffset(Long offset) {
		this.offset = offset;
	}

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

	public Byte getType() {
		return type;
	}

	public void setType(Byte type) {
		this.type = type;
	}
    
    
}
