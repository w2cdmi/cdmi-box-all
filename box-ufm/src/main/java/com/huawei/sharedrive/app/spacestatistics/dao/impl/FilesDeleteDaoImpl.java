package com.huawei.sharedrive.app.spacestatistics.dao.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.spacestatistics.dao.FilesDeleteDao;
import com.huawei.sharedrive.app.spacestatistics.domain.FilesAdd;
import com.huawei.sharedrive.app.spacestatistics.domain.FilesDelete;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

@Service("filesDeleteDao")
@SuppressWarnings("deprecation")
public class FilesDeleteDaoImpl extends CacheableSqlMapClientDAO implements FilesDeleteDao
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FilesDeleteDaoImpl.class);
    
    @SuppressWarnings("unchecked")
    @Override
    public List<FilesDelete> getAllFilesDelete()
    {
        List<FilesDelete> deletedSpaces = (List<FilesDelete>) sqlMapClientTemplate.queryForList("FilesDelete.getUserDelete");
        if (null == deletedSpaces)
        {
            return null;
        }
        List<Long> nodeIds;
        for (FilesDelete deletedSpace : deletedSpaces)
        {
            nodeIds = getNodeIdsByOwnedBy(deletedSpace.getOwnedBy());
            if (isCacheSupported() && nodeIds != null)
            {
                getCacheClient().setCache(FilesDelete.CACHE_KEY_PREFIX_DELETEDNODES
                    + deletedSpace.getOwnedBy(),
                    nodeIds);
            }
        }
        return deletedSpaces;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void deleteFilesDeleteByCache(FilesAdd changedSpace)
    {
        if (isCacheSupported()
            && getCacheClient().getCache(FilesDelete.CACHE_KEY_PREFIX_DELETEDNODES
                + changedSpace.getOwnedBy()) != null)
        {
            List<Long> nodeIds = (List<Long>) getCacheClient().getCache(FilesDelete.CACHE_KEY_PREFIX_DELETEDNODES
                + changedSpace.getOwnedBy());
            FilesDelete deletedFile = new FilesDelete();
            for (long nodeId : nodeIds)
            {
                deletedFile.setNodeId(nodeId);
                deletedFile.setOwnedBy(changedSpace.getOwnedBy());
                deletedFile.setAccountId(changedSpace.getAccountId());
                sqlMapClientTemplate.delete("FilesDelete.deleteByNodeId", deletedFile);
            }
            getCacheClient().deleteCache(FilesDelete.CACHE_KEY_PREFIX_DELETEDNODES
                + changedSpace.getOwnedBy());
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<Long> getNodeIdsByOwnedBy(long userId)
    {
        return (List<Long>) sqlMapClientTemplate.queryForList("FilesDelete.getdeletedNodes", userId);
    }
    
    @Override
    public void deleteByUserId(long userId)
    {
        sqlMapClientTemplate.delete("FilesDelete.deleteByUserId", userId);
    }
    
    @Override
    public void insert(FilesDelete deletedFile)
    {
        if (null == deletedFile)
        {
            return;
        }
        try
        {
            sqlMapClientTemplate.insert("FilesDelete.insert", deletedFile);
        }
        catch(Exception e)
        {
            if(StringUtils.contains(e.getMessage(), "Duplicate entry"))
            {
                LOGGER.debug("", e);
            }
            else
            {
                throw e;
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<FilesDelete> getDeletedAccountInfo()
    {
        return (List<FilesDelete>) sqlMapClientTemplate.queryForList("FilesDelete.getDeletedAccountInfo");
        
    }
}
