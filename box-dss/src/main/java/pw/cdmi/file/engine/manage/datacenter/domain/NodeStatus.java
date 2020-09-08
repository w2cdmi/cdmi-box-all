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
package pw.cdmi.file.engine.manage.datacenter.domain;

/**
 * 节点状态
 * 
 * @author s90006125
 * 
 */
public enum NodeStatus
{
    Disable(0), Enable(1);
    
    private int code;
    
    private NodeStatus(int code)
    {
        this.code = code;
    }
    
    public int getCode()
    {
        return code;
    }
    
    public static NodeStatus parseStatus(int status)
    {
        for (NodeStatus s : NodeStatus.values())
        {
            if (status == s.getCode())
            {
                return s;
            }
        }
        
        return null;
    }
}
