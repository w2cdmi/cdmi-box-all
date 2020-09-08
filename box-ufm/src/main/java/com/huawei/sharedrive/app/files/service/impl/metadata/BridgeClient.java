package com.huawei.sharedrive.app.files.service.impl.metadata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.exception.InternalServerErrorException;
import com.huawei.sharedrive.app.exception.NoSuchFileException;
import com.huawei.sharedrive.app.utils.PropertiesUtils;

import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.StreamResponse;

@Service
public class BridgeClient
{
    @Autowired
    private BridgeTokenHelper bridgeTokenHelper;
    
    @Autowired
    private RestClient fpClientService;
    
    
    
    public File getFileByBridge(long userId, MetadataTempFile tempFileObj, boolean isMaster)
    {
        String url = getDbFileBridgePath(userId, tempFileObj, isMaster);
        Map<String, String> headers = new HashMap<String, String>(1);
        String token = bridgeTokenHelper.createBridgeToken(userId);
        headers.put("Authorization", token);
        StreamResponse response = fpClientService.performGetStreamByUri(url, headers);
        if(response.getStatusCode() == 404)
        {
            throw new NoSuchFileException(url);
        }
        if(response.getStatusCode() != 200)
        {
            throw new InternalServerErrorException("Get temp dbfile status is " +response.getStatusCode() + url);
        }
        InputStream in = null;
        OutputStream output = null;
        try
        {
            in = response.getInputStream();
            File file = new File(getLocalFilePath() + tempFileObj.getFileName());
            output = new FileOutputStream(file);
            IOUtils.copy(in, output);
            return file;
        }
        catch(IOException e)
        {
            throw new InternalServerErrorException("Can not pull the dbFile from bridge", e);
        }
        finally
        {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(output);
        }
    }
    
    
    private String getDbFileBridgePath(long userId, MetadataTempFile tempFileObj, boolean isMaster)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getProtocal())
            .append("://");
        if(isMaster)
        {
            sb.append(tempFileObj.getMainDbIP());
        }
        else
        {
            sb.append(tempFileObj.getSlaveDbIP());
        }
        sb.append(':')
            .append(getPort())
            .append("/filebridge/api/v2/dbfile/")
            .append(userId)
            .append('/')
            .append(tempFileObj.getFileName())
            .append("/download");
        return sb.toString();
    }

    
    private String getLocalFilePath()
    {
        return PropertiesUtils.getProperty("filebridge.localFile", "/opt/tomcat_ufm/temp/",  PropertiesUtils.BundleName.BRIDGE);
    }
    
    
    
    private String getPort()
    {
        return PropertiesUtils.getProperty("filebridge.port", "5080", PropertiesUtils.BundleName.BRIDGE);
    }
    
    private String getProtocal()
    {
        return PropertiesUtils.getProperty("filebridge.protocal", "http", PropertiesUtils.BundleName.BRIDGE);
    }
    
}
