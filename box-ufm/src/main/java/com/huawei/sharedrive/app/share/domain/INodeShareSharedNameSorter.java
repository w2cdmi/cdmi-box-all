package com.huawei.sharedrive.app.share.domain;

import java.io.Serializable;
import java.util.Comparator;

/**
 * 被共享者名称排序器
 * 
 * @author l90003768
 * 
 */
public class INodeShareSharedNameSorter implements Comparator<INodeShare>, Serializable
{
    
    private static final long serialVersionUID = 2211714001217145812L;
    
    @Override
    public int compare(INodeShare o1, INodeShare o2)
    {
        return o1.getSharedUserName().compareTo(o2.getSharedUserName());
    }
    
}
