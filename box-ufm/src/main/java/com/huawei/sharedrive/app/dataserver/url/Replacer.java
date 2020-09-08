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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author s90006125
 *
 */
public class Replacer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Replacer.class);
    
    private String source;
    private String target;
    
    public String replace(String str)
    {
        if(StringUtils.isBlank(str))
        {
            LOGGER.info("source url is null.");
            return null;
        }
        
        if(str.indexOf(this.getSource()) < 0)
        {
            return null;
        }
        
        return str.replaceFirst(this.getSource(), this.getTarget());
    }

    public String getSource()
    {
        return source;
    }
    public void setSource(String source)
    {
        this.source = source;
    }
    public String getTarget()
    {
        return target;
    }
    public void setTarget(String target)
    {
        this.target = target;
    }
}
