/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package com.huawei.sharedrive.isystem.logfile.domain;


/**
 * 
 * @author s90006125
 *
 */
public class LogAgentNode
{
    private String nodeId;
    private String nodeName;
    
    public LogAgentNode()
    {
    }
    
    public LogAgentNode(String nodeName)
    {
        this.nodeName = nodeName;
    }
    
    public String getNodeId()
    {
        return nodeId;
    }
    public void setNodeId(String nodeId)
    {
        this.nodeId = nodeId;
    }
    public String getNodeName()
    {
        return nodeName;
    }
    public void setNodeName(String nodeName)
    {
        this.nodeName = nodeName;
    }
}
