package com.huawei.sharedrive.app.filelabel.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * Desc : 文件标签与文件节点关联实体
 * Author: 77235 
 * Date : 2016年11月28日
 */
public class FileLabelLink implements Serializable {
    
    private static final long serialVersionUID = 1L;
    /** 主鍵 */
    private long id;
    /** 标签编号 */
    private long labelId;
    /** 文件实体编号 */
    private long inodeId;
    /** 绑定操作用户 */
    private long bindUserId;
    /** 文件所属虚拟用户 */
    private long ownedBy;
    /** 绑定时间 */
    private Date bindTime = new Date();
    /** 附加属性:表后缀 */
    private int tableSuffix;

    public FileLabelLink() {
        super();
    }

    public FileLabelLink(long labelId, long inodeId) {
        this.labelId = labelId;
        this.inodeId = inodeId;
    }
    
    public FileLabelLink(long labelId, long inodeId, long bindUserId) {
        this.labelId = labelId;
        this.inodeId = inodeId;
        this.ownedBy = bindUserId;
        this.bindUserId = bindUserId;
    }
    
    public long getLabelId() {
        return labelId;
    }
    
    public void setLabelId(long labelId) {
        this.labelId = labelId;
    }
    
    public long getInodeId() {
        return inodeId;
    }
    
    public void setInodeId(long inodeId) {
        this.inodeId = inodeId;
    }
    
    public long getBindUserId() {
        return bindUserId;
    }
    
    public void setBindUserId(long bindUserId) {
        this.bindUserId = bindUserId;
    }
    
    public Date getBindTime() {
        return bindTime;
    }
    
    public void setBindTime(Date bindTime) {
        this.bindTime = bindTime;
    }

    public int getTableSuffix() {
        return tableSuffix;
    }

    public void setTableSuffix(int tableSuffix) {
        this.tableSuffix = tableSuffix;
    }

    public long getOwnedBy() {
        return ownedBy;
    }

    public void setOwnedBy(long ownedBy) {
        this.ownedBy = ownedBy;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        
        result = prime * result + (int) (inodeId ^ (inodeId >>> 32));
        result = prime * result + (int) (labelId ^ (labelId >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        
        FileLabelLink other = (FileLabelLink) obj;
        if (inodeId == other.inodeId && labelId == other.labelId)
            return true;
      
        return false;
    }

}
