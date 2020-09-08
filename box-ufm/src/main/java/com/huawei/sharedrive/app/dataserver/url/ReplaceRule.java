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
package com.huawei.sharedrive.app.dataserver.url;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author s90006125
 *
 */
public class ReplaceRule
{
    private List<Integer> networkTypes = new ArrayList<Integer>(5);
    
    private List<Replacer> replacers = new ArrayList<Replacer>(10);

    public List<Integer> getNetworkTypes()
    {
        return networkTypes;
    }

    public void setNetworkTypes(List<Integer> networkTypes)
    {
        this.networkTypes = networkTypes;
    }

    public List<Replacer> getReplacers()
    {
        return replacers;
    }

    public void setReplacers(List<Replacer> replacers)
    {
        this.replacers = replacers;
    }
}
