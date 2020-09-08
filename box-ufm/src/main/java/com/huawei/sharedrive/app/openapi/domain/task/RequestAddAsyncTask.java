package com.huawei.sharedrive.app.openapi.domain.task;

import java.util.List;

import com.huawei.sharedrive.app.openapi.domain.user.LinkUser;

/**
 * 添加异步任务请求
 * @author l90003768
 *
 */
public class RequestAddAsyncTask
{
    
    private Long destFolderId;

    private Long destOwnerId;
    
    private LinkUser link;

    private List<RequestNode> srcNodeList;

    private Long srcOwnerId;

    private String type;
    
    private Boolean autoRename;
    
    private String startPoint;
    
    private String endPoint;

    public Long getDestFolderId()
    {
        return destFolderId;
    }

    public Long getDestOwnerId()
    {
        return destOwnerId;
    }

    public LinkUser getLink()
    {
        return link;
    }

    public List<RequestNode> getSrcNodeList()
    {
        return srcNodeList;
    }

    public Long getSrcOwnerId()
    {
        return srcOwnerId;
    }

    public String getType()
    {
        return type;
    }
    
    public void setDestFolderId(Long destFolderId)
    {
        this.destFolderId = destFolderId;
    }
    
    public void setDestOwnerId(Long destOwnerId)
    {
        this.destOwnerId = destOwnerId;
    }
    
    public void setLink(LinkUser link)
    {
        this.link = link;
    }
    
    public void setSrcNodeList(List<RequestNode> srcNodeList)
    {
        this.srcNodeList = srcNodeList;
    }

    public void setSrcOwnerId(Long srcOwnerId)
    {
        this.srcOwnerId = srcOwnerId;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public Boolean getAutoRename()
    {
        return autoRename;
    }

    public void setAutoRename(Boolean autoRename)
    {
        this.autoRename = autoRename;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }
}
