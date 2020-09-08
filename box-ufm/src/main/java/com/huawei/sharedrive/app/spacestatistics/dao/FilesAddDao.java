package com.huawei.sharedrive.app.spacestatistics.dao;

import java.util.List;

import com.huawei.sharedrive.app.spacestatistics.domain.FilesAdd;

public interface FilesAddDao
{
    List<FilesAdd> getAllFilesAdd();
    
    void deleteFilesAddByMaxNodeId(FilesAdd statisticed);
    
    List<FilesAdd> getAddedAccountInfo();
    
    void insert(FilesAdd addedFile);
}
