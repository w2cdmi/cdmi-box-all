package com.huawei.sharedrive.app.spacestatistics.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.spacestatistics.dao.FilesAddDao;
import com.huawei.sharedrive.app.spacestatistics.domain.FilesAdd;

@Service("filesAddDao")
@SuppressWarnings("deprecation")
public class FilesAddDaoImpl implements FilesAddDao
{
    @Autowired
    protected SqlMapClientTemplate sqlMapClientTemplate;
    
    @SuppressWarnings("unchecked")
    @Override
    public List<FilesAdd> getAllFilesAdd()
    {
        return (List<FilesAdd>) sqlMapClientTemplate.queryForList("FilesAdd.getUserAdd");
    }
    
    @Override
    public void deleteFilesAddByMaxNodeId(FilesAdd statisticed)
    {
        if (statisticed.getNodeId() == null)
        {
            sqlMapClientTemplate.delete("FilesAdd.deleteByUserId", statisticed);
        }
        else
        {
            sqlMapClientTemplate.delete("FilesAdd.deleteByMaxNodeId", statisticed);
        }
        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<FilesAdd> getAddedAccountInfo()
    {
        return (List<FilesAdd>) sqlMapClientTemplate.queryForList("FilesAdd.getAddedAccountInfo");
        
    }
    
    @Override
    public void insert(FilesAdd addedFile)
    {
        if (null == addedFile)
        {
            return;
        }
        sqlMapClientTemplate.insert("FilesAdd.insert", addedFile);
    }
    
}
