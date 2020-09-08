package com.huawei.sharedrive.app.openapi.domain.node;

import java.util.List;

import com.huawei.sharedrive.app.files.domain.INode;

public class MigrationFolderRequest {
	
	private List<INode> srcNodes;
	
	    
	public List<INode> getSrcNodes() {
		return srcNodes;
	}

	public void setSrcNodes(List<INode> srcNodes) {
		this.srcNodes = srcNodes;
	}

	// 目标文件夹拥有者ID
	private Long destOwnerId;
	
	// 目标文件夹拥有者ID
    private Long enterpriseUserId;
	    
	public Long getEnterpriseUserId() {
		return enterpriseUserId;
	}

	public void setEnterpriseUserId(Long enterpriseUserId) {
		this.enterpriseUserId = enterpriseUserId;
	}

	// 目标文件夹ID
	private Long destParent;
	
	private String parentFolderName;

	public Long getDestOwnerId() {
		return destOwnerId;
	}

	public void setDestOwnerId(Long destOwnerId) {
		this.destOwnerId = destOwnerId;
	}

	public Long getDestParent() {
		return destParent;
	}

	public void setDestParent(Long destParent) {
		this.destParent = destParent;
	}

	public String getParentFolderName() {
		return parentFolderName;
	}

	public void setParentFolderName(String parentFolderName) {
		this.parentFolderName = parentFolderName;
	}

	
	
	
	

}
