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
package com.huawei.sharedrive.isystem.logfile.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.huawei.sharedrive.isystem.logfile.dao.LogAgentDao;

import pw.cdmi.core.restrpc.RestClient;

/**
 * 
 * @author s90006125
 *         
 */
public class LogAgentBaseService
{
    
    @Autowired
    protected LogAgentDao logAgentDao;
    
    @Autowired
    protected RestClient logAgentClientService;
    
    @Value("${logagent.service.ac.protocol}")
    protected String acProtocol;
    
    @Value("${logagent.service.ac.address}")
    protected String acAddress;
    
    @Value("${logagent.service.ac.port}")
    protected int acPort;
    
    @Value("${logagent.service.name}")
    protected String serviceContextPath;
    
    @Value("${logagent.service.dc.protocol}")
    protected String dcProtocol;
    
    @Value("${logagent.service.dc.port}")
    protected int dcPort;
    
    /**
     * 获取中心侧logAgent的访问地址
     * 
     * @return
     */
    protected String getCenterAgentUri()
    {
        return new StringBuilder(StringUtils.trimToEmpty(acProtocol)).append("://")
            .append(StringUtils.trimToEmpty(acAddress))
            .append(":")
            .append(acPort)
            .append("/")
            .append(StringUtils.trimToEmpty(serviceContextPath))
            .toString();
    }
    
}
