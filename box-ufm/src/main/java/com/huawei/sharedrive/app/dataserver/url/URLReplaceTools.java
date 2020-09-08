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

import com.huawei.sharedrive.app.dataserver.domain.DataAccessURLInfo;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponseV1;
import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponseV2;

import pw.cdmi.uam.domain.AuthApp;

/**
 * 
 * @author s90006125
 *
 */
public class URLReplaceTools
{
    private static final Logger LOGGER = LoggerFactory.getLogger(URLReplaceTools.class);
    
    private URLReplaceUtils uploadUrlReplaceUtils;

    private URLReplaceUtils downloadUrlReplaceUtils;
    
    /**
     * 替换下载地址
     * @param authApp
     * @param urlinfo
     */
    public void replaceDownloadUrl(AuthApp authApp, DataAccessURLInfo urlinfo, UserToken userToken)
    {
        if (null == downloadUrlReplaceUtils || null == authApp || null == urlinfo || null == userToken)
        {
            LOGGER.warn("some object is null. {}, {}, {}, {}", downloadUrlReplaceUtils, authApp, urlinfo, userToken);
            return;
        }
        
        String url = downloadUrlReplaceUtils.replace(authApp.getAuthAppId(),
            userToken.getNetworkType(),
            urlinfo.getDownloadUrl());
        if (StringUtils.isBlank(url))
        {
            return;
        }
        
        urlinfo.setDownloadUrl(url);
    }
    
    /**
     * 替换上传地址
     * @param appId
     * @param rsp
     */
    public void replaceUploadUrlForV1(String appId, FilePreUploadResponseV1 rsp)
    {
        if (null == uploadUrlReplaceUtils || StringUtils.isBlank(appId) || null == rsp)
        {
            LOGGER.warn("some object is null. {}, {}, {}", uploadUrlReplaceUtils, appId, rsp);
            return;
        }
        
        String url = uploadUrlReplaceUtils.replace(appId, rsp.getUrl());
        if (StringUtils.isBlank(url))
        {
            return;
        }
        
        rsp.setUrl(url);
    }
    
    /**
     * 替换上传地址
     * @param appId
     * @param rsp
     */
    public void replaceUploadUrlForV2(String appId, FilePreUploadResponseV2 rsp)
    {
        if (null == uploadUrlReplaceUtils || StringUtils.isBlank(appId) || null == rsp)
        {
            LOGGER.warn("some object is null. {}, {}, {}", uploadUrlReplaceUtils, appId, rsp);
            return;
        }
        
        String url = uploadUrlReplaceUtils.replace(appId, rsp.getUploadUrl());
        if (StringUtils.isBlank(url))
        {
            return;
        }
        
        rsp.setUploadUrl(url);
    }
    
    public URLReplaceUtils getUploadUrlReplaceUtils()
    {
        return uploadUrlReplaceUtils;
    }

    public void setUploadUrlReplaceUtils(URLReplaceUtils uploadUrlReplaceUtils)
    {
        this.uploadUrlReplaceUtils = uploadUrlReplaceUtils;
    }

    public URLReplaceUtils getDownloadUrlReplaceUtils()
    {
        return downloadUrlReplaceUtils;
    }

    public void setDownloadUrlReplaceUtils(URLReplaceUtils downloadUrlReplaceUtils)
    {
        this.downloadUrlReplaceUtils = downloadUrlReplaceUtils;
    }
}
