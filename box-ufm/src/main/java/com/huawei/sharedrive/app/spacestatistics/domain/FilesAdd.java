package com.huawei.sharedrive.app.spacestatistics.domain;

public class FilesAdd extends ChangedFilesCommon
{
    
    private static final long serialVersionUID = 3883760845809007221L;
    
    public FilesAdd(long ownedBy, Long nodeId, Long accountId, Long size)
    {
        super(ownedBy, nodeId, accountId, size);
    }
    
    public FilesAdd(long ownedBy, Long nodeId)
    {
        super(ownedBy, nodeId);
    }
    
    public FilesAdd()
    {
        super();
    }
    
}
