package com.huawei.sharedrive.app.files.service.impl.metadata;

import com.huawei.sharedrive.app.utils.PropertiesUtils;

public final class MetadataFileTools
{
    private MetadataFileTools()
    {
        
    }
    
    public static String getMetadataFilePath()
    {
        String folder = PropertiesUtils.getProperty("dbfile.location", "/opt/tomcat_fb/temp/", PropertiesUtils.BundleName.BRIDGE);
        if(folder.charAt(folder.length() - 1) == '/')
        {
            return folder;
        }
        return folder + '/';
    }
    
    public static String getMedatadaFileName(long ownerId)
    {
        return ownerId + "_" + System.currentTimeMillis();
    }
}
