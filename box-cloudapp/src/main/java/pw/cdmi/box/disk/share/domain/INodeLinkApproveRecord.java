package pw.cdmi.box.disk.share.domain;

import java.util.Date;

/**
 * 文件外发审批记录表
 *
 * @author Administrator
 */
public class INodeLinkApproveRecord {
    private long id;
    private String linkCode;
    private Date approveAt;
    private long approveBy;
    private byte status;
    private String comment;
    private String approveByName;

    public INodeLinkApproveRecord() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLinkCode() {
        return linkCode;
    }

    public void setLinkCode(String linkCode) {
        this.linkCode = linkCode;
    }

    public Date getApproveAt() {
        return approveAt;
    }

    public void setApproveAt(Date approveAt) {
        this.approveAt = approveAt;
    }

    public long getApproveBy() {
        return approveBy;
    }

    public void setApproveBy(long approveBy) {
        this.approveBy = approveBy;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getApproveByName() {
        return approveByName;
    }

    public void setApproveByName(String approveByName) {
        this.approveByName = approveByName;
    }
}
