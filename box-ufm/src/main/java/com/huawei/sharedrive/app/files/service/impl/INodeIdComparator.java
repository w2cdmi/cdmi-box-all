package com.huawei.sharedrive.app.files.service.impl;

import java.io.Serializable;
import java.util.Comparator;

import com.huawei.sharedrive.app.files.domain.INode;

public class INodeIdComparator implements Comparator<INode>, Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 3896874499242052364L;

    @Override
    public int compare(INode arg0, INode arg1)
    {
        return (int)(arg1.getId() - arg0.getId());
    }
    
    
}
