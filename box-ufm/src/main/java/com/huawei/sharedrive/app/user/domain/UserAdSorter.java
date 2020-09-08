/*
 * Copyright Huawei Technologies Co.,Ltd. 2013-2014. All rights reserved.
 */
package com.huawei.sharedrive.app.user.domain;

import java.io.Serializable;
import java.util.Comparator;

/**
 * AD用户排序器，首先根据是否注册，然后根据名称排序
 * 
 * @author l90003768
 */
public class UserAdSorter implements Comparator<User>, Serializable
{
    
    private static final long serialVersionUID = 6126560364988068482L;

    @Override
    public int compare(User o1, User o2)
    {
        int res = o1.getType() - o2.getType();
        if (res != 0)
        {
            return res;
        }
        return o1.getName().compareTo(o2.getName());
    }
    
}
