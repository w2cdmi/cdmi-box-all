package pw.cdmi.box.disk.client.domain.task;

import java.util.List;

public class RequestAddAsyncTask
{
    
    public static final String TYPE_CLEAN = "cleanTrash";
    
    public static final String TYPE_COPY = "copy";
    
    public static final String TYPE_DELETE = "delete";
    
    public static final String TYPE_MOVE = "move";
    
    public static final String TYPE_RESTORE = "restoreTrash";
    
    
    private Boolean autoRename;

    private Long destFolderId;
    
    private Long destOwnerId;

    private LinkUser link;

    private List<RequestNode> srcNodeList;

    private Long srcOwnerId;
    
    private String type;
    
    private String startPoint;
    
    private String endPoint;

    public Boolean getAutoRename()
    {
        return autoRename;
    }

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
    
    public void setAutoRename(Boolean autoRename)
    {
        this.autoRename = autoRename;
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
