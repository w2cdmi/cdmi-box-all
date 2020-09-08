package pw.cdmi.box.uam.httpclient.domain;

import java.io.Serializable;

public class RestAppStatisticsResponse implements Serializable
{
    private static final long serialVersionUID = -6575661159768984394L;
    
    private Long fileCount;// 文件总数，包含历史版本和回收站中的文件数量
    
    private Long spaceCount;// 空间总数
    
    private Long spaceUsed;// 已经使用的空间配额，单位为字节
    
    private String type;// 统计类型
    
    public Long getFileCount()
    {
        return fileCount;
    }
    
    public Long getSpaceCount()
    {
        return spaceCount;
    }
    
    public Long getSpaceUsed()
    {
        return spaceUsed;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setFileCount(Long fileCount)
    {
        this.fileCount = fileCount;
    }
    
    public void setSpaceCount(Long spaceCount)
    {
        this.spaceCount = spaceCount;
    }
    
    public void setSpaceUsed(Long spaceUsed)
    {
        this.spaceUsed = spaceUsed;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
}