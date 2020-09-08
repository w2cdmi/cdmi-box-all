package com.huawei.sharedrive.app.files.service.impl.metadata;

import java.io.File;

public class MetadataTempFile
{
    private String fileName;


    private String mainDbIP;


    private String slaveDbIP;
    
    private File file;


    public String getFileName()
    {
        return fileName;
    }


    public String getMainDbIP()
    {
        return mainDbIP;
    }
    
    public String getSlaveDbIP()
    {
        return slaveDbIP;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }


    public void setMainDbIP(String mainDbIP)
    {
        this.mainDbIP = mainDbIP;
    }

    
    public void setSlaveDbIP(String slaveDbIP)
    {
        this.slaveDbIP = slaveDbIP;
    }


    public File getFile()
    {
        return file;
    }


    public void setFile(File file)
    {
        this.file = file;
    }
    
}
