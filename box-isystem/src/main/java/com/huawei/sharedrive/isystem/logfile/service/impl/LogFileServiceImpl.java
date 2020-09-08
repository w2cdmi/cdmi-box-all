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
package com.huawei.sharedrive.isystem.logfile.service.impl;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.exception.BusinessException;
import com.huawei.sharedrive.isystem.logfile.domain.LogFile;
import com.huawei.sharedrive.isystem.logfile.domain.QueryCondition;
import com.huawei.sharedrive.isystem.logfile.domain.QueryResult;
import com.huawei.sharedrive.isystem.logfile.service.DownloadFileResponse;
import com.huawei.sharedrive.isystem.logfile.service.LogAgentBaseService;
import com.huawei.sharedrive.isystem.logfile.service.LogFileService;

import pw.cdmi.core.restrpc.domain.StreamResponse;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;

/**
 * 
 * @author s90006125
 *         
 */
@Service("logFileService")
public class LogFileServiceImpl extends LogAgentBaseService implements LogFileService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(LogFileServiceImpl.class);
    
    @Override
    public QueryResult searchFile(int clusterId, QueryCondition condition)
    {
        
        Map<String, String> headers = new HashMap<String, String>(10);
        
        String url = getCenterAgentUri() + "/api/v2/logagent/logfile/search/" + clusterId;
        LOGGER.info("search condition : " + JsonUtils.toJson(condition));
        TextResponse response = logAgentClientService.performJsonPostTextResponseByUri(url,
            headers,
            condition);
        if (response.getStatusCode() == HttpStatus.NOT_FOUND.value())
        {
            LOGGER.warn("LogAgent Not Found [ " + clusterId + " ]");
            return null;
        }
        else if (response.getStatusCode() != HttpStatus.OK.value())
        {
            String message = "search logfile failed [ " + ReflectionToStringBuilder.toString(response) + " ]";
            LOGGER.warn(message);
            throw new BusinessException(message);
        }
        LOGGER.info(response.getResponseBody());
        return JsonUtils.stringToObject(response.getResponseBody(), QueryResult.class);
    }
    
    @Override
    public DownloadFileResponse downLoadLogFile(int clusterId, String id) throws IOException
    {
        
        Map<String, String> headers = new HashMap<String, String>(10);
        
        String url = getCenterAgentUri() + "/api/v2/logagent/logfile/" + clusterId + '/' + id;
        
        StreamResponse response = logAgentClientService.performGetStreamByUri(url, headers);
        
        if (response.getStatusCode() != HttpStatus.OK.value())
        {
            String message = "download log file failed [ " + clusterId + ";  " + id + ";  "
                + ReflectionToStringBuilder.toString(response) + " ]";
            LOGGER.warn(message);
            throw new BusinessException(message);
        }
        
        Map<String, String> responseHeaders = response.getResponseHeaders();
        
        LogFile logFile = new LogFile();
        logFile.setSize(Long.parseLong(responseHeaders.get("Content-Length")));
        logFile.setFileName(URLDecoder.decode(responseHeaders.get("logFileName"), "UTF-8"));
        logFile.setInputStream(response.getInputStream());
        
        return new DownloadFileResponse(logFile, response);
    }
    
}
