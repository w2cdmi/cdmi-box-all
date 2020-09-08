package pw.cdmi.box.disk.share.domain;

import java.util.Date;
public class INodeLinkApprove {

	
    /**审批完成**/
    public static final byte APPROVE_STATUS_COMPLETE=2;
    /**审批中**/
    public static final byte APPROVE_STATUS_APPROVAL=1;
    /**驳回**/
    public static final byte APPROVE_STATUS_REJECT=3;
    
    /**链接已删除**/
    public static final byte LINK_STATUS_DELETE=0;
    /**链接正常**/
    public static final byte LINK_STATUS_NORMAL=1;
    /**
     * 
     */
    private static final long serialVersionUID = -5418594937509226948L;
    
    private String linkCode;
    
    private long accountId;
    
    private byte status;
    
    private byte linkStatus;
    
    private long approveBy;
	// 审批人名称
	private String  approveName;
    
	private String applyReason;
    
    private Date approveAt;
    
    private long linkOwner;
    
    private String linkOwnerName;
    
	private Date startTime;
    
    private Date endTime;

    private long nodeId;
    
    private String nodeName;
    
    private byte type;
    
    public INodeLinkApprove(long approve,long accountId,long iNodeId,byte status,long linkOwner,String applyReason){
    	this.approveBy=approve;
    	this.accountId=accountId;
    	this.nodeId=iNodeId;
    	this.status=status;
    	this.linkOwner=linkOwner;
    	this.applyReason=applyReason;
    }
    
    public INodeLinkApprove(){
    	
    }
	public String getLinkCode() {
		return linkCode;
	}

	public void setLinkCode(String linkCode) {
		this.linkCode = linkCode;
	}

	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public long getNodeId() {
		return nodeId;
	}
	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}
	public long getApproveBy() {
		return approveBy;
	}

	public void setApproveBy(long approveBy) {
		this.approveBy = approveBy;
	}

	public String getApplyReason() {
		return applyReason;
	}

	public void setApplyReason(String applyReason) {
		this.applyReason = applyReason;
	}

	public Date getApproveAt() {
		return approveAt;
	}

	public void setApproveAt(Date approveAt) {
		this.approveAt = approveAt;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public long getLinkOwner() {
		return linkOwner;
	}
	public void setLinkOwner(long linkOwner) {
		this.linkOwner = linkOwner;
	}


	public String getLinkOwnerName() {
		return linkOwnerName;
	}
	public void setLinkOwnerName(String linkOwnerName) {
		this.linkOwnerName = linkOwnerName;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public byte getLinkStatus() {
		return linkStatus;
	}

	public void setLinkStatus(byte linkStatus) {
		this.linkStatus = linkStatus;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public String getApproveName() {
		return approveName;
	}

	public void setApproveName(String approveName) {
		this.approveName = approveName;
	}
    
    

}
