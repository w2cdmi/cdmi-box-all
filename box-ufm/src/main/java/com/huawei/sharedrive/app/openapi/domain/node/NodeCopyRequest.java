package com.huawei.sharedrive.app.openapi.domain.node;

import java.util.List;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

/**
 * 节点复制请求对象
 * 
 * @author  t90006461
 * @version  CloudStor CSE Service Platform Subproject, 2014-5-22
 * @see  
 * @since  
 */
public class NodeCopyRequest
{
    // 与目标文件夹下的文件名/文件夹名冲突时, 是否自动重命名
    private Boolean autoRename;
    
    // 目标文件夹拥有者ID
    private Long destOwnerId;
    
    // 目标文件夹ID
    private Long destParent;
    
    private List<INode>  srcNodes;
    
    // 外链提取码
    private LinkForCopyRequest link;
    
    public NodeCopyRequest()
    {
        
    }
    
    public NodeCopyRequest(Long destParent, Long destOwnerId, Boolean autoRename)
    {
        this.destParent = destParent;
        this.destOwnerId = destOwnerId;
        this.autoRename = autoRename;
    }
    
    public void checkParameter() throws InvalidParamException
    {
        FilesCommonUtils.checkNonNegativeIntegers(destOwnerId, destParent);
        if (autoRename == null)
        {
            throw new InvalidParamException();
        }
    }
    
    public Long getDestOwnerId()
    {
        return destOwnerId;
    }
    
    public Long getDestParent()
    {
        return destParent;
    }
    
    public LinkForCopyRequest getLink()
    {
        return link;
    }
    
    public Boolean isAutoRename()
    {
        return autoRename;
    }
    
    public void setAutoRename(Boolean autoRename)
    {
        this.autoRename = autoRename;
    }
    
    public void setDestOwnerId(Long destOwnerId)
    {
        this.destOwnerId = destOwnerId;
    }
    
    public void setDestParent(Long destParent)
    {
        this.destParent = destParent;
    }
    
    public void setLink(LinkForCopyRequest link)
    {
        this.link = link;
    }

	public List<INode> getSrcNodes() {
		return srcNodes;
	}

	public void setSrcNodes(List<INode> srcNodes) {
		this.srcNodes = srcNodes;
	}
    
    
}
