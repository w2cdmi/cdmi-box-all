package com.huawei.sharedrive.app.plugins.preview.dao;

import java.util.List;

import com.huawei.sharedrive.app.plugins.preview.domain.PreviewObject;

public interface PreviewObjectDao
{
    
    void create(PreviewObject obj);
    
    PreviewObject get(String sourceObjectId, long accountId);
    
    PreviewObject selectForUpdate(String sourceObjectId, long accountId);
    
    List<PreviewObject> getAllBySourceObjectId(String sourceObjectId);
    
    int updateConvertStartTime(PreviewObject obj);
    
    int updateConvertResult(PreviewObject obj);
    
    int updateConvertRestart(PreviewObject obj);
    
    int delete(String sourceObjectId, long accountId);
    
}