package com.huawei.sharedrive.app.event.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.utils.BusinessConstants;

/**
 * 持久化的事件, 用于处理可靠性要求较高的事务
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-5-04
 * @see
 * @since
 */
public class PersistentEvent implements Serializable
{
    public static final int PRIORITY_NORMAL = 5;
    
    public static final int PRIORITY_LOW = 1;
    
    public static final int PRIORITY_HIGH = 9;
    
    private static final long serialVersionUID = 1785096515007623379L;
    
    private EventType eventType;
    
    private Long ownedBy;
    
    private Long createdBy;
    
    private Long nodeId;
    
    private Long parentId;
    
    private String nodeName;
    
    private Byte nodeType;
    
    private Integer priority;
    
    private Map<String, String> params;
    
    public void addParameter(String key, Object value)
    {
        if (params == null)
        {
            params = new HashMap<String, String>(BusinessConstants.INITIAL_CAPACITIES);
        }
        params.put(key, String.valueOf(value));
    }
    
    public INode getBaseNodeInfo()
    {
        INode node = new INode();
        node.setId(nodeId == null ? 0 : nodeId);
        node.setName(nodeName);
        node.setType(nodeType == null ? 0 : nodeType);
        node.setOwnedBy(ownedBy == null ? 0 : ownedBy);
        node.setParentId(parentId == null ? 0 : parentId);
        return node;
    }
    
    public EventType getEventType()
    {
        return eventType;
    }
    
    public Long getNodeId()
    {
        return nodeId;
    }
    
    public String getNodeName()
    {
        return nodeName;
    }
    
    public Byte getNodeType()
    {
        return nodeType;
    }
    
    public Long getOwnedBy()
    {
        return ownedBy;
    }
    
    public String getParameter(String key)
    {
        if (params == null || StringUtils.isBlank(key))
        {
            return null;
        }
        return params.get(key);
    }
    
    public Map<String, String> getParams()
    {
        return params;
    }
    
    public Long getParentId()
    {
        return parentId;
    }
    
    public Integer getPriority()
    {
        return priority;
    }
    
    public void setEventType(EventType eventType)
    {
        this.eventType = eventType;
    }
    
    public void setNodeId(Long nodeId)
    {
        this.nodeId = nodeId;
    }
    
    public void setNodeName(String nodeName)
    {
        this.nodeName = nodeName;
    }
    
    public void setNodeType(Byte nodeType)
    {
        this.nodeType = nodeType;
    }
    
    public void setOwnedBy(Long ownedBy)
    {
        this.ownedBy = ownedBy;
    }
    
    public void setParams(Map<String, String> params)
    {
        this.params = params;
    }
    
    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }
    
    public void setPriority(Integer priority)
    {
        this.priority = priority;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }

    public Long getCreatedBy()
    {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy)
    {
        this.createdBy = createdBy;
    }
    
    
}
