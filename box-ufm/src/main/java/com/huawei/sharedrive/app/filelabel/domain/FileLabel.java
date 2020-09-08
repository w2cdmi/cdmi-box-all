package com.huawei.sharedrive.app.filelabel.domain;

import java.util.Date;

import com.huawei.sharedrive.app.filelabel.dto.FileLabelRequestDto;

/**
 * 
 * Desc  : 文件标签实体
 * Author: 77235
 * Date	 : 2016年11月28日
 */
public class FileLabel extends BaseFileLabelInfo {
    private static final long serialVersionUID = 1L;
    /** 创建人 */
	private long createBy;
	/** 创建时间 */
	private Date createTime;
	/** 绑定次数 */
	private long bindedTimes;
	/** 企业编号 */
	private long enterpriseId;
	/** 綁定類型  */
	private int labelType;

	public long getCreateBy() {
		return createBy;
	}

	public void setCreateBy(long createBy) {
		this.createBy = createBy;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public long getBindedTimes() {
		return bindedTimes;
	}

	public void setBindedTimes(long bindedTimes) {
		this.bindedTimes = bindedTimes;
	}

	public long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	
    public int getLabelType() {
        return labelType;
    }

    public void setLabelType(int labelType) {
        this.labelType = labelType;
    }

    public FileLabel(FileLabelRequestDto fileLabelRequest) {
        this.enterpriseId = fileLabelRequest.getEnterpriseId();
        this.createTime = new Date();
        this.labelType = fileLabelRequest.getBindType();
        this.createBy = fileLabelRequest.getBindUserId();
        setLabelName(fileLabelRequest.getLabelName());
    }

    public FileLabel() {
        super();
    }
}
