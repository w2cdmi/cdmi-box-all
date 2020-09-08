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

import java.io.Closeable;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import com.huawei.sharedrive.isystem.exception.BusinessException;
import com.huawei.sharedrive.isystem.logfile.domain.LogFile;

import pw.cdmi.core.restrpc.domain.StreamResponse;

/**
 * 
 * @author s90006125
 *
 */
public class DownloadFileResponse implements Closeable
{
    private LogFile logFile;
    private StreamResponse response;
    public DownloadFileResponse(LogFile logFile)
    {
        if(null == logFile)
        {
            throw new BusinessException("create DownloadFile response failed.");
        }
        this.logFile = logFile;
    }
    
    public DownloadFileResponse(LogFile logFile, StreamResponse response)
    {
        if(null == logFile
            || null == response)
        {
            throw new BusinessException("create DownloadFile response failed.");
        }
        this.logFile = logFile;
        this.response = response;
    }
    public LogFile getLogFile()
    {
        return logFile;
    }
    public StreamResponse getResponse()
    {
        return response;
    }
    @Override
    public void close() throws IOException
    {
        IOUtils.closeQuietly(logFile.getInputStream());
        IOUtils.closeQuietly(response);
    }
}
