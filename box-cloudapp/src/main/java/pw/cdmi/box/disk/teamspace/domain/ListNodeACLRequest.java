package pw.cdmi.box.disk.teamspace.domain;

import pw.cdmi.box.disk.utils.FilesCommonUtils;
import pw.cdmi.core.exception.InvalidParamException;

public class ListNodeACLRequest
{
    private static final int DEFAULT_LIMIT = 100;
    
    private static final long DEFAULT_OFFSET = 0L;
    
    private Long nodeId;
    
    private Integer limit;
    
    private Long offset;
    
    public ListNodeACLRequest()
    {
        limit = DEFAULT_LIMIT;
        offset = DEFAULT_OFFSET;
    }
    
    public ListNodeACLRequest(Integer limit, Long offset)
    {
        this.limit = limit != null ? limit : DEFAULT_LIMIT;
        this.offset = offset != null ? offset : DEFAULT_OFFSET;
    }
    
    public void checkParameter() throws InvalidParamException
    {
        if (nodeId != null)
        {
            FilesCommonUtils.checkNonNegativeIntegers(nodeId);
        }
        
        if (limit != null && limit < 1)
        {
            throw new InvalidParamException();
        }
        if (offset != null && offset < 0)
        {
            throw new InvalidParamException();
        }
    }
    
    public Integer getLimit()
    {
        return limit;
    }
    
    public Long getNodeId()
    {
        return nodeId;
    }
    
    public Long getOffset()
    {
        return offset;
    }
    
    public void setLimit(Integer limit)
    {
        this.limit = limit;
    }
    
    public void setNodeId(Long nodeId)
    {
        this.nodeId = nodeId;
    }
    
    public void setOffset(Long offset)
    {
        this.offset = offset;
    }
    
}
