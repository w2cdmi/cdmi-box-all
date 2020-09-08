package com.huawei.sharedrive.app.spacestatistics.domain;

public class FilesDelete extends ChangedFilesCommon
{
    
    private static final long serialVersionUID = 7420793845671185269L;
    
    public static final String CACHE_KEY_PREFIX_DELETEDNODES = "user_deleted_nodes_";
    
    public FilesDelete(long ownedBy, long nodeId, long accountId, Long size)
    {
        super(ownedBy, nodeId, accountId, size);
    }
    
    public FilesDelete()
    {
        super();
    }
    
}
