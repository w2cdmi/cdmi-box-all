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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;

/**
 * 
 * @author s90006125
 *
 */
public class URLReplaceUtils
{
    private static final Logger LOGGER = LoggerFactory.getLogger(URLReplaceUtils.class);
    
    // appIds --> rules
    private Map<String, ReplaceRule> rules = new ConcurrentHashMap<String, ReplaceRule>();
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    public String replace(String appId, Integer networkType, String url)
    {
        UserToken userToken = userTokenHelper.getCurrentToken();
        
        if(LOGGER.isDebugEnabled())
        {
            LOGGER.debug("user : {}; appId : {}; networkType : {}; sourceUrl : {}; ", null == userToken ? "null" : userToken.getCloudUserId(), appId, networkType, url);
        }
        
        if(StringUtils.isBlank(appId)
            || null == networkType
            || null == rules
            || StringUtils.isBlank(url))
        {
            return url;
        }
        
        ReplaceRule rule = rules.get(appId);
        if(null == rule)
        {
            if(LOGGER.isDebugEnabled())
            {
                LOGGER.debug("no rule for app : {} ", appId);
            }
            return url;
        }

        if(!rule.getNetworkTypes().contains(networkType))
        {
            return url;
        }
        
        String result = null;
        List<Replacer> replacers = rule.getReplacers();
        for(Replacer replace : replacers)
        {
            result = replace.replace(url);
            if(StringUtils.isNotBlank(result))
            {
                LOGGER.info("replace [ {} ] to [ {} ] for app [ {} ] in networkType [ {} ].", url, result, appId, networkType);
                return result;
            }
        }
        
        return url;
    }
    
    public String replace(String appId, String url)
    {
        UserToken userToken = userTokenHelper.getCurrentToken();
        
        if(LOGGER.isDebugEnabled())
        {
            LOGGER.debug("user : {}; appId : {}; sourceUrl : {}; ", null == userToken ? "null" : userToken.getCloudUserId(), appId, url);
        }
        
        if(StringUtils.isBlank(appId)
            || null == rules
            || StringUtils.isBlank(url))
        {
            return url;
        }
        
        ReplaceRule rule = rules.get(appId);
        if(null == rule)
        {
            if(LOGGER.isDebugEnabled())
            {
                LOGGER.debug("no rule for app : {} ", appId);
            }
            return url;
        }

        String result = null;
        List<Replacer> replacers = rule.getReplacers();
        for(Replacer replace : replacers)
        {
            result = replace.replace(url);
            if(StringUtils.isNotBlank(result))
            {
                LOGGER.info("replace [ {} ] to [ {} ] for app [ {} ].", url, result, appId);
                return result;
            }
        }
        
        return url;
    }
    
    public Map<String, ReplaceRule> getRules()
    {
        return rules;
    }

    public void setRules(Map<String, ReplaceRule> rules)
    {
        this.rules = rules;
    }
}
