package com.huawei.sharedrive.uam.log.domain;

public class ExportExcel
{
    
    private String plyName;
    
    private int num;
    
    private String fileName;
    
    private String filetype;
    
    public String getFileName()
    {
        return fileName;
    }
    
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }
    
    public String getFiletype()
    {
        return filetype;
    }
    
    public void setFiletype(String filetype)
    {
        this.filetype = filetype;
    }
    
    public ExportExcel()
    {
        super();
    }
    
    public ExportExcel(String plyName, int num)
    {
        super();
        this.plyName = plyName;
        this.num = num;
    }
    
    public String getPlyName()
    {
        return plyName;
    }
    
    public void setPlyName(String plyName)
    {
        this.plyName = plyName;
    }
    
    public int getNum()
    {
        return num;
    }
    
    public void setNum(int num)
    {
        this.num = num;
    }
    
    public ExportExcel(int num, String fileName, String filetype)
    {
        super();
        this.num = num;
        this.fileName = fileName;
        this.filetype = filetype;
    }
    
}
