package com.huawei.sharedrive.app.spacestatistics.dao;

import java.util.List;

import com.huawei.sharedrive.app.spacestatistics.domain.FilesAdd;
import com.huawei.sharedrive.app.spacestatistics.domain.FilesDelete;

public interface FilesDeleteDao
{
    List<FilesDelete> getAllFilesDelete();
    
    void deleteFilesDeleteByCache(FilesAdd changedSpace);
    
    List<Long> getNodeIdsByOwnedBy(long userId);
    
    void deleteByUserId(long userId);
    
    void insert(FilesDelete deletedFile);
    
    List<FilesDelete> getDeletedAccountInfo();
}
